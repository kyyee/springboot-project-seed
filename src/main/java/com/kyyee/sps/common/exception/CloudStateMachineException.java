package com.kyyee.sps.common.exception;

import com.kyyee.framework.common.exception.BaseException;

import java.io.Serial;

public class CloudStateMachineException extends BaseException {

    @Serial
    private static final long serialVersionUID = -4564990485275621298L;

    public CloudStateMachineException(String message) {
        super(message);
    }
}
