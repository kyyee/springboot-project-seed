package com.kyyee.sps.common.component.task.manager;

import com.kyyee.sps.common.component.workflow.WorkFlowCallback;
import com.kyyee.sps.model.BaseTaskEntity;

public interface TaskProcessor<T extends BaseTaskEntity> extends WorkFlowCallback {

    String getType();

    void process(DelayTask<T> task);
}
