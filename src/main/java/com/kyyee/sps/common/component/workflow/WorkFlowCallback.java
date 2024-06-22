package com.kyyee.sps.common.component.workflow;

import com.kyyee.framework.common.exception.BaseException;

public interface WorkFlowCallback {
    void succeed(WorkFlowContext ctx);

    void fail(WorkFlowContext ctx, BaseException error);
}
