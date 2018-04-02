/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.exception;

/**
 * @author kyyee
 * 非法参数异常
 */
public class IllegalParameterException extends RuntimeException {
    private static final long serialVersionUID = 725153515363953655L;

    public IllegalParameterException() {
    }

    public IllegalParameterException(String message) {
        super(message);
    }

    public IllegalParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalParameterException(Throwable cause) {
        super(cause);
    }

    public IllegalParameterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
