package com.kyyee.sps.common.component.task.strategy;

import com.kyyee.sps.common.component.workflow.WorkFlowCallback;
import com.kyyee.sps.model.BaseTaskEntity;

public interface TaskHandleStrategy<T extends BaseTaskEntity> extends WorkFlowCallback {

    /**
     * 任务类型
     *
     * @return 任务类型
     */
    String getType();

    /**
     * 任务carry on
     *
     * @param task
     */
    void process(T task);
}
