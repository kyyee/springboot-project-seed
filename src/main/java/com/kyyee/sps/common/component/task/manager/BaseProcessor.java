package com.kyyee.sps.common.component.task.manager;

import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.sps.common.component.workflow.AsyncWorkFlowChain;
import com.kyyee.sps.common.component.workflow.WorkFlowChainState;
import com.kyyee.sps.common.component.workflow.WorkFlowContext;
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
import java.lang.reflect.Type;
import java.util.Optional;

@Slf4j
public abstract class BaseProcessor<T extends BaseTaskEntity, R extends ServletRequest> implements TaskProcessor<T> {

    @Resource
    private WorkFlowChainMapper chainMapper;
    @Resource
    private WorkFlowMapper flowMapper;

    private final Class<R> paramClazz;

    public BaseProcessor(Type paramClazz) {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.paramClazz = (Class<R>) genericSuperclass.getActualTypeArguments()[0];
    }

    public Type getParamClass() {
        return this.paramClazz;
    }

    protected abstract TaskProcessor<T> getProcessor();

    protected abstract void setFlowChain(AsyncWorkFlowChain asyncWorkFlowChain);

    protected abstract String getChainName(R param);

    public void process(DelayTask<T> task) {
        T taskData = task.getTaskData();
        if ("yes".equals(taskData.getFinish())) {
            log.info("task:{}, grId:{}, type:{}, state:{} finished...", taskData.getId(), taskData.getGrId(), taskData.getType(), taskData.getState());
            return;
        }
        R request = JSON.toBean(taskData.getContext(), this.paramClazz);
        AsyncWorkFlowChain asyncWorkFlowChain = new AsyncWorkFlowChain(chainMapper, flowMapper);
        setFlowChain(asyncWorkFlowChain);
        WorkFlowContext workFlowContext = new WorkFlowContext();
        workFlowContext.put("request", request);
        workFlowContext.put("taskData", taskData);
        asyncWorkFlowChain.setContext(workFlowContext);
        String chainName = getChainName(request);
        asyncWorkFlowChain.setName(chainName);

        Optional<WorkFlowChain> flowChain = chainMapper.wrapper().eq(WorkFlowChain::getName, chainName).one();
        if (flowChain.isPresent() && StringUtils.hasText(flowChain.get().getUuid())) {
            try {
                if (WorkFlowChainState.ProcessDone.equals(flowChain.get().getState())) {
                    taskData.setFinish("yes");
                    getProcessor().succeed(workFlowContext);
                } else if (WorkFlowChainState.RollbackDone.equals(flowChain.get().getState())) {
                    taskData.setFinish("yes");
                    String message = taskData.getMessage();
                    if (StringUtils.hasText(message)) {
                        message += " task carry on rollback done";
                    } else {
                        message = "task carry on rollback done";
                    }
                    getProcessor().fail(workFlowContext, ServiceException.of(BaseErrorCode.SYS_INTERNAL_ERROR, message));
                } else {
                    asyncWorkFlowChain.carryOn(flowChain.get(), getProcessor());
                }
            } catch (Throwable t) {
                log.info("task:{}, grId:{}, type:{}, state:{} carry on failed...", taskData.getId(), taskData.getGrId(), taskData.getType(), taskData.getState());
            }
            return;
        }
        asyncWorkFlowChain.build().run(getProcessor());
    }
}
