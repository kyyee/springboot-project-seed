package com.kyyee.sps.common.exception.code;

import com.kyyee.framework.common.exception.ErrorCodeDefaultImpl;
import com.kyyee.framework.common.exception.IErrorCode;

public enum CustomErrorCode {

    EXTERNAL_API_CALL_ERROR("10000", "外部接口调用异常"),
    ;

    private final IErrorCode error;

    CustomErrorCode(String code, String msg) {
        this.error = new ErrorCodeDefaultImpl(code, msg);
    }

    public IErrorCode of() {
        return this.error;
    }

    public String getCode() {
        return this.error.getCode();
    }

    public String getMsg() {
        return this.error.getMsg();
    }
}
