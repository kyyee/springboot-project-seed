package com.kyyee.sps.common.exception;

import com.kyyee.framework.common.exception.BaseException;

import java.io.Serial;

public class ServiceException extends BaseException {
    @Serial
    private static final long serialVersionUID = 3155526842494527937L;

    public ServiceException(String message) {
        super(message);
    }
}
