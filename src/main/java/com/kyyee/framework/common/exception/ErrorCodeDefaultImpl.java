package com.kyyee.framework.common.exception;


import com.kyyee.framework.common.utils.SpringUtils;

public class ErrorCodeDefaultImpl implements IErrorCode {
    //前五位编码   xx xxx
    private static final String errorCodePrefix;

    static {
        errorCodePrefix = SpringUtils.getProperty("kyyee.error-code-prefix");
    }

    private final String code;
    private final String msg;

    public ErrorCodeDefaultImpl(String code, String msg) {
        this.code = errorCodePrefix + code;
        this.msg = msg;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
