package com.kyyee.sps.common.component.task.flow;

import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import com.kyyee.sps.common.component.workflow.AsyncWorkFlow;
import com.kyyee.sps.common.component.workflow.AsyncWorkFlowChain;
import com.kyyee.sps.common.component.workflow.WorkFlowContext;
import com.kyyee.sps.common.enums.TaskFinishEnum;
import com.kyyee.sps.common.exception.ServiceException;
import com.kyyee.sps.mapper.BaseMapper;
import com.kyyee.sps.model.BaseEntity;
import com.kyyee.sps.model.BaseTaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public abstract class BaseFlow<R extends BaseEntity, T extends BaseTaskEntity> implements AsyncWorkFlow {
    private final BaseMapper<R, Long> rBaseMapper;
    private final BaseMapper<T, Long> tBaseMapper;
    private final Class<R> rClass;

    protected BaseFlow(BaseMapper<T, Long> tBaseMapper, BaseMapper<R, Long> rBaseMapper) {
        this.tBaseMapper = tBaseMapper;
        this.rBaseMapper = rBaseMapper;
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.rClass = (Class<R>) genericSuperclass.getActualTypeArguments()[0];
    }

    public R selectResource(WorkFlowContext context) {
        T task = selectTask(context);
        String grId = task.getGrId();
        if (!StringUtils.hasText(grId)) {
            throw ServiceException.of(BaseErrorCode.SYS_INTERNAL_ERROR.of(), "资源 grId 不存在");
        }
        R resource = (R) context.get(WorkFlowContext.RESOURCE);
        if (resource != null && grId.equals(resource.getGrId())) {
            return resource;
        }
        final Lock lock = new ReentrantLock();
        lock.lock();
        try {
            Optional<R> one = rBaseMapper.wrapper().eq(R::getGrId, grId).one();
            if (!one.isPresent()) {
                throw ServiceException.of(BaseErrorCode.RESULT_EMPTY_ERROR.of(), "资源 {} 不存在", grId);
            }
            context.put(WorkFlowContext.RESOURCE, one.get());
            return one.get();
        } finally {
            lock.unlock();
        }
    }

    public T selectTask(WorkFlowContext context) {
        T task = (T) context.get(WorkFlowContext.TASK_DATA);
        if (task == null) {
            throw ServiceException.of(BaseErrorCode.RESULT_EMPTY_ERROR.of(), "任务不存在");
        }
        return task;
    }

    public void process(AsyncWorkFlowChain flowChain, WorkFlowContext workFlowContext) {
        T task = selectTask(workFlowContext);
        if (!StringUtils.hasText(task.getFlowChainId())) {
            task.setFlowChainId(flowChain.getUuid());
        }
        String flowName = getName();
        task.setState(flowName);

        try {
            log.info("task:{}, grId:{}, type:{}, state:{} starting...", task.getId(), task.getGrId(), task.getType(), task.getState());
            run(workFlowContext, task);
            log.info("task:{}, grId:{}, type:{}, state:{} finish...", task.getId(), task.getGrId(), task.getType(), task.getState());
            // 成功埋点
            this.success(workFlowContext);
            flowChain.runNext(workFlowContext);
        } catch (Exception e) {
            if (TaskFinishEnum.YES.equals(task.getFinish())) {
                // 失败埋点
                this.fail(workFlowContext, e);
                log.error("task:{}, grId:{}, type:{}, state:{} failed...", task.getId(), task.getGrId(), task.getType(), task.getState());
                throw e;
            }
            if (e instanceof BaseException baseException
                && BaseErrorCode.RESULT_EMPTY_ERROR.getCode().equals(baseException.getCode())) {
                task.setMessage(e.getMessage());
                task.setFinish(TaskFinishEnum.YES);
                // 失败埋点
                this.fail(workFlowContext, e);
                log.error("task:{}, grId:{}, type:{}, state:{} failed, result is empty...", task.getId(), task.getGrId(), task.getType(), task.getState());
                throw e;
            }
            if (Duration.between(task.getUpdateAt(), LocalDateTime.now()).toMillis() > task.getTimeout()) {
                task.setMessage("任务超时结束");
                task.setFinish(TaskFinishEnum.YES);
                // 失败埋点
                this.fail(workFlowContext, e);
                log.error("task:{}, grId:{}, type:{}, state:{} failed, task timeout...", task.getId(), task.getGrId(), task.getType(), task.getState());
                throw ServiceException.of(BaseErrorCode.SYS_INTERNAL_ERROR, "task timeout");
            }
            log.info("task:{}, grId:{}, type:{}, state:{} waiting...", task.getId(), task.getGrId(), task.getType(), task.getState(), e);
        } finally {
            task.preUpdate(task);
            int taskUpdated = tBaseMapper.updateByPrimaryKey(task);

            R resource = selectResource(workFlowContext);
            resource.preUpdate(resource);
            int resourceUpdated = rBaseMapper.updateByPrimaryKey(resource);
            if (taskUpdated > 0 && resourceUpdated > 0) {
                log.info("task:{}, grId:{}, type:{}, state:{} updated success...", task.getId(), task.getGrId(), task.getType(), task.getState());
            }
        }
    }

    protected abstract void run(WorkFlowContext workFlowContext, T task);

    protected abstract void success(WorkFlowContext workFlowContext);

    protected abstract void fail(WorkFlowContext workFlowContext, Exception e);
}
