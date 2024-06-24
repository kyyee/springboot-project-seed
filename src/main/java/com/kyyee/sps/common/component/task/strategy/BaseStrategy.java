package com.kyyee.sps.common.component.task.strategy;

import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.sps.common.component.workflow.AsyncWorkFlowChain;
import com.kyyee.sps.common.component.workflow.WorkFlowChainState;
import com.kyyee.sps.common.component.workflow.WorkFlowContext;
import com.kyyee.sps.common.enums.TaskFinishEnum;
import com.kyyee.sps.common.exception.ServiceException;
import com.kyyee.sps.common.utils.JSON;
import com.kyyee.sps.mapper.primary.WorkFlowChainMapper;
import com.kyyee.sps.mapper.primary.WorkFlowMapper;
import com.kyyee.sps.model.BaseTaskEntity;
import com.kyyee.sps.model.primary.WorkFlowChain;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

@Slf4j
public abstract class BaseStrategy<T extends BaseTaskEntity, R extends ServletRequest> implements TaskHandleStrategy<T> {

    @Resource
    private WorkFlowChainMapper chainMapper;
    @Resource
    private WorkFlowMapper flowMapper;

    private final Class<R> paramClazz;

    protected BaseStrategy() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.paramClazz = (Class<R>) genericSuperclass.getActualTypeArguments()[1];
    }

    protected abstract void setFlowChain(AsyncWorkFlowChain asyncWorkFlowChain, R param);

    protected abstract String getChainName(R param);

    public void process(T taskData) {
        if (TaskFinishEnum.YES.equals(taskData.getFinish())) {
            log.info("task:{}, grId:{}, type:{}, state:{} finished...", taskData.getId(), taskData.getGrId(), taskData.getType(), taskData.getState());
            return;
        }
        R request = JSON.toBean(taskData.getContext(), this.paramClazz);
        AsyncWorkFlowChain asyncWorkFlowChain = new AsyncWorkFlowChain(chainMapper, flowMapper);
        setFlowChain(asyncWorkFlowChain, request);
        WorkFlowContext workFlowContext = new WorkFlowContext();
        workFlowContext.put(WorkFlowContext.REQUEST, request);
        workFlowContext.put(WorkFlowContext.TASK_DATA, taskData);
        asyncWorkFlowChain.setContext(workFlowContext);
        String chainName = getChainName(request);
        asyncWorkFlowChain.setName(chainName);

        Optional<WorkFlowChain> flowChainOptional = chainMapper.wrapper()
            .eq(WorkFlowChain::getName, chainName).one();
        if (flowChainOptional.isPresent()) {
            WorkFlowChain flowChain = flowChainOptional.get();
            if (StringUtils.hasText(flowChain.getUuid())) {
                try {
                    if (WorkFlowChainState.ProcessDone.equals(flowChain.getState())) {
                        taskData.setFinish(TaskFinishEnum.YES);
                        // 成功埋点
                        this.succeed(workFlowContext);
                    } else if (WorkFlowChainState.RollbackDone.equals(flowChain.getState())) {
                        taskData.setFinish(TaskFinishEnum.YES);
                        String message = taskData.getMessage();
                        if (StringUtils.hasText(message)) {
                            message += " task carry on rollback done";
                        } else {
                            message = "task carry on rollback done";
                        }
                        // 失败埋点
                        this.fail(workFlowContext, ServiceException.of(BaseErrorCode.SYS_INTERNAL_ERROR, message));
                    } else {
                        asyncWorkFlowChain.carryOn(flowChain, this);
                    }
                } catch (Throwable t) {
                    log.info("task:{}, grId:{}, type:{}, state:{} carry on failed...", taskData.getId(), taskData.getGrId(), taskData.getType(), taskData.getState());
                }
                return;
            }
        }
        asyncWorkFlowChain.build().run(this);
    }
}
