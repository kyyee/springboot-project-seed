package com.kyyee.sps.common.exception;


import com.kyyee.framework.common.exception.BaseException;

import java.io.Serial;

public class WorkFlowException extends BaseException {

    @Serial
    private static final long serialVersionUID = 8123221235771489244L;

    public WorkFlowException(String message) {
        super(message);
    }
}
