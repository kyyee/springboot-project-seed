package com.kyyee.sps.common.component.task.manager;

import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.sps.common.component.task.strategy.TaskHandleStrategy;
import com.kyyee.sps.common.exception.ServiceException;
import com.kyyee.sps.mapper.BaseMapper;
import com.kyyee.sps.model.BaseTaskEntity;
import jakarta.servlet.ServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public abstract class TaskManager<T extends BaseTaskEntity, R extends ServletRequest> implements ApplicationRunner {

    private final BaseMapper<T, Long> tBaseMapper;
    private final Integer taskCheckTime;
    private final Integer workerNum;
    private final Integer taskMaxDelay;
    private static final Map<String, TaskHandleStrategy<?>> taskStrategyContainer = new HashMap<>();
    // 执行异步任务的线程池
    private final ThreadPoolExecutor threadPool;
    // 异步任务保存在该队列中等待执行
    private final DelayQueue<DelayTask<T>> queue;

    @Getter
    // 用于避免多副本重复拉起异步任务
    private String localName;

    private final Class<T> tClass;
    private final Type rClass;


    protected TaskManager(BaseMapper<T, Long> tBaseMapper,
                          @Value("${kyyee.task.check-time:90}") Integer taskCheckTime,
                          @Value("${kyyee.task.worker:3}") Integer worker,
                          @Value("${kyyee.task.max-delay:60}") Integer taskMaxDelay) {
        this.tBaseMapper = tBaseMapper;
        this.taskCheckTime = taskCheckTime;
        this.workerNum = worker;
        this.taskMaxDelay = taskMaxDelay;
        this.threadPool = new ThreadPoolExecutor(worker, (int) Math.pow(worker, worker), 1L, TimeUnit.MINUTES, new ArrayBlockingQueue<>((int) Math.pow(worker, worker)));
        this.threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        queue = new DelayQueue<>();
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.tClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
        this.rClass = genericSuperclass.getActualTypeArguments()[1];
        try {
            String hostname = System.getenv("HOSTNAME");
            if (StringUtils.hasText(hostname)) {
                this.localName = hostname;
            } else {
                InetAddress localHost = InetAddress.getLocalHost();
                hostname = localHost.getHostName();
                if (StringUtils.hasText(hostname)) {
                    this.localName = hostname;
                } else {
                    throw ServiceException.of(BaseErrorCode.SYS_INTERNAL_ERROR, "get hostname failed.");
                }
            }
        } catch (UnknownHostException e) {
            log.error("get hostname failed, task can't carry on");
        }
    }

    protected abstract String getName();

    public List<Long> taskIds() {
        return queue.stream().map(task -> task.getTaskData().getId()).toList();
    }

    public void loadUnfinish() {
        List<T> unfinishTasks = tBaseMapper.wrapper()
            .eq(BaseTaskEntity::getFinish, "no")
            .list();
        if (ObjectUtils.isEmpty(unfinishTasks)) {
            log.info("can't find unfinish task, skip...");
            return;
        }
        for (T unfinishTask : unfinishTasks) {
            if (StringUtils.hasText(this.localName) && this.localName.equals(unfinishTask.getHostname())) {
                // 本实例的任务，直接入队
                queue(unfinishTask);
                continue;
            }
            // 更新时间超过了taskCheckTime，说明对应的副本宕机，且没有启动成功
            if (Duration.between(unfinishTask.getUpdateAt(), LocalDateTime.now()).toSeconds() >= taskCheckTime) {
                // 正常30秒更新，若90秒未更新，则说明当前任务未被其他副本持有，任务入队
                String oldHostname = unfinishTask.getHostname();
                unfinishTask.setUpdateAt(LocalDateTime.now());
                unfinishTask.setHostname(this.localName);
                int updated = tBaseMapper.wrapper()
                    .eq(BaseTaskEntity::getHostname, oldHostname)
                    .eq(BaseTaskEntity::getId, unfinishTask.getId())
                    .updateSelective(unfinishTask);
                if (updated > 0) {
                    // 被当前实例获取，任务入队
                    queue(unfinishTask);
                }
                // 当前任务已被其他实例获取执行，跳过
            }
            // 当前任务被其他副本持有
        }
    }

    public void queue(T task) {
        if (taskIds().contains(task.getId())) {
            log.info("task: {} type: {} is exist in this service", task.getId(), task.getState());
            return;
        }
        DelayTask<T> delayTask = new DelayTask<>(task);
        delayTask.setDelayMaxTime(taskMaxDelay);
        Lock lock = new ReentrantLock(true);
        try {
            if (lock.tryLock(3, TimeUnit.SECONDS)) {
                if (queue.offer(delayTask)) {
                    log.info("task:{} type:{} param:{} push queue success.", task.getId(), task.getType(), task.getContext());
                }
            }
        } catch (InterruptedException e) {
            log.warn("push task:{} in queue failed, can't acquire lock", task.getId());
            Thread.currentThread().interrupt();
            throw ServiceException.of(BaseErrorCode.SYS_INTERNAL_ERROR.of(), "push task:{} in queue failed", task.getId());
        } finally {
            lock.unlock();
        }
    }

    public void requeue(DelayTask<T> delayTask) {
        delayTask.resetDelay();
        if (queue.offer(delayTask)) {
            log.info("task:{} type:{} param:{} push requeue success.", delayTask.getTaskData().getId(), delayTask.getTaskData().getType(), delayTask.getTaskData().getContext());
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("init worker starting...");
        this.execute();
        log.info("init worker complete...");

        log.info("init daemon starting...");
        this.monitor();
        log.info("init daemon complete...");
    }

    private void execute() {
        loadUnfinish();
        for (int i = 0; i < this.workerNum; i++) {
            threadPool.execute(() -> {
                while (true) {
                    DelayTask<T> delayTask = null;
                    try {
                        delayTask = queue.take();
                        if (ObjectUtils.isEmpty(delayTask)) {
                            continue;
                        }
                        T taskData = delayTask.getTaskData();
                        if (ObjectUtils.isEmpty(taskData)) {
                            continue;
                        }
                        Thread thread = Thread.currentThread();
                        thread.setName("%s-%s-%s".formatted(taskData.getType(), taskData.getGrId(), taskData.getReqId()));
                        TaskHandleStrategy<T> taskHandleStrategy = getTaskStrategy(taskData.getType());
                        if (ObjectUtils.isEmpty(taskHandleStrategy)) {
                            log.warn("task:{}, grId:{}, type:{} processor is not exist...", taskData.getId(), taskData.getGrId(), taskData.getType());
                            requeue(delayTask);
                            continue;
                        }
                        // 设置上下文
                        buildContext(taskData);
                        log.info("task:{}, grId:{}, type:{} process...", taskData.getId(), taskData.getGrId(), taskData.getType());
                        taskHandleStrategy.process(delayTask.getTaskData());
                        if (!delayTask.finish()) {
                            requeue(delayTask);
                            log.warn("task:{}, grId:{}, type:{} process failed, repush in queue, delay time:{}...", taskData.getId(), taskData.getGrId(), taskData.getType(), delayTask.getDelayTime());
                            continue;
                        }
                        if (queue.remove(delayTask)) {
                            log.info("task:{}, grId:{}, type:{} process complete...", taskData.getId(), taskData.getGrId(), taskData.getType());
                        }
                    } catch (Exception e) {
                        log.info("init worker failed...");
                        if (!ObjectUtils.isEmpty(delayTask)) {
                            requeue(delayTask);
                        }
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
    }

    private void monitor() {
        // 守护线程
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Thread thread = Thread.currentThread();
                thread.setName("DelayTaskMonitor");
                log.debug("workerNum:{}, current alive delay queue size:{}", workerNum, queue.size());
            }
        }, 0, TimeUnit.MILLISECONDS.convert(30, TimeUnit.SECONDS));
    }

    public abstract R buildContext(T taskData);

    public static void registerTaskStrategy(TaskHandleStrategy<?> processor) {
        // 注册 strategy
        synchronized (taskStrategyContainer) {
            TaskHandleStrategy<?> taskHandleStrategy = taskStrategyContainer.get(processor.getType());
            // 不允许重复
            if (!ObjectUtils.isEmpty(taskHandleStrategy)) {
                log.error("task processor:{} repeat.", taskHandleStrategy.getType());
                throw ServiceException.of(BaseErrorCode.SYS_INTERNAL_ERROR.of(), "task processor:{} repeat.", taskHandleStrategy.getType());
            }
            taskStrategyContainer.put(processor.getType(), processor);
        }
    }

    public TaskHandleStrategy<T> getTaskStrategy(String type) {
        TaskHandleStrategy<?> taskHandleStrategy = taskStrategyContainer.get(type);
        if (ObjectUtils.isEmpty(taskHandleStrategy)) {
            throw ServiceException.of(BaseErrorCode.SYS_INTERNAL_ERROR.of(), "task processor:{} is not exist.", taskHandleStrategy.getType());
        }
        return (TaskHandleStrategy<T>) taskHandleStrategy;
    }
}
