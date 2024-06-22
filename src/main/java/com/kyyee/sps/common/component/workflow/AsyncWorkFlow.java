package com.kyyee.sps.common.component.workflow;

import com.kyyee.sps.common.exception.WorkFlowException;

public interface AsyncWorkFlow {
    void process(AsyncWorkFlowChain chain, WorkFlowContext ctx) throws WorkFlowException;

    void rollback(WorkFlowContext ctx);

    String getName();
}
