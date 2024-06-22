package com.kyyee.sps.common.component.task.flow;

import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import com.kyyee.sps.common.component.workflow.AsyncWorkFlow;
import com.kyyee.sps.common.component.workflow.AsyncWorkFlowChain;
import com.kyyee.sps.common.component.workflow.WorkFlowContext;
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

@Slf4j
public abstract class BaseFlow<D extends BaseEntity, T extends BaseTaskEntity> implements AsyncWorkFlow {
    private final BaseMapper<D, Long> dBaseMapper;
    private final BaseMapper<T, Long> tBaseMapper;
    private final Class<D> dClass;

    public BaseFlow(BaseMapper<T, Long> tBaseMapper, BaseMapper<D, Long> dBaseMapper) {
        this.tBaseMapper = tBaseMapper;
        this.dBaseMapper = dBaseMapper;
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.dClass = (Class<D>) genericSuperclass.getActualTypeArguments()[0];
    }

    private final Object resourceQueryLock = new Object();

    public D selectResource(WorkFlowContext context) {
        T task = selectTask(context);
        String grId = task.getGrId();
        if (!StringUtils.hasText(grId)) {
            throw ServiceException.of(BaseErrorCode.SYS_INTERNAL_ERROR);
        }
        D resource = (D) context.get("resource");
        if (resource != null && grId.equals(resource.getGrId())) {
            return resource;
        }
        synchronized (resourceQueryLock) {
            Optional<D> one = dBaseMapper.wrapper().eq(D::getGrId, grId).one();
            if (one.isEmpty()) {
                throw ServiceException.of(BaseErrorCode.RESULT_EMPTY_ERROR);
            }
            context.put("resource", one.get());
            return one.get();
        }
    }

    public T selectTask(WorkFlowContext context) {
        T task = (T) context.get("taskData");
        if (task == null) {
            throw ServiceException.of(BaseErrorCode.SYS_INTERNAL_ERROR);
        }
        return task;
    }

    public void process(AsyncWorkFlowChain flowChain, WorkFlowContext workFlowContext) {
        String flowName = getName();
        T task = selectTask(workFlowContext);

        task.setState(flowName);
        if (!StringUtils.hasText(task.getFlowChainId())) {
            task.setFlowChainId(flowChain.getUuid());
        }

        try {
            log.info("task:{}, grId:{}, type:{}, state:{} starting...", task.getId(), task.getGrId(), task.getType(), task.getState());
            run(workFlowContext, task);
            log.info("task:{}, grId:{}, type:{}, state:{} finish...", task.getId(), task.getGrId(), task.getType(), task.getState());
        } catch (BaseException e) {
            if (BaseErrorCode.RESULT_EMPTY_ERROR.getCode().equals(e.getCode())) {
                task.setMessage(e.getMessage());
                task.setFinish("yes");
                throw e;
            }
            log.info("task:{}, grId:{}, type:{}, state:{} waiting...", task.getId(), task.getGrId(), task.getType(), task.getState(), e);
            return;
        } catch (Exception e) {
            if ("yes".equalsIgnoreCase(task.getFinish())) {
                // 失败埋点
                log.error("task:{}, grId:{}, type:{}, state:{} failed...", task.getId(), task.getGrId(), task.getType(), task.getState());
                throw e;
            }
            if (Duration.between(task.getUpdateAt(), LocalDateTime.now()).toMillis() > task.getTimeout()) {
                task.setMessage("任务超时结束");
                task.setFinish("yes");
                log.error("task:{}, grId:{}, type:{}, state:{} failed...", task.getId(), task.getGrId(), task.getType(), task.getState());
                throw ServiceException.of(BaseErrorCode.SYS_INTERNAL_ERROR, "task timeout");
            }
            log.info("task:{}, grId:{}, type:{}, state:{} waiting...", task.getId(), task.getGrId(), task.getType(), task.getState(), e);
            return;
        } finally {
            try {
                task.preUpdate(task);
                tBaseMapper.updateByPrimaryKey(task);

                D resource = selectResource(workFlowContext);
                resource.preUpdate(resource);
                dBaseMapper.updateByPrimaryKey(resource);
                log.info("task:{}, grId:{}, type:{}, state:{} updated...", task.getId(), task.getGrId(), task.getType(), task.getState());
            } catch (Exception e) {
                log.error("task:{}, grId:{}, type:{}, state:{} failed...", task.getId(), task.getGrId(), task.getType(), task.getState(), e);
                throw e;
            }
        }
        // 成功埋点
        flowChain.runNext(workFlowContext);
    }

    public abstract String getName();

    protected abstract void run(WorkFlowContext workFlowContext, T task);
}
