/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.v1.constant;

/**
 * @author kyyee
 * 异常返回前端的错误码枚举
 */
public enum ErrorCodeEnum {
    UNAUTHORIZED(-401, "认证失败"),
    FORBIDDEN(-403, "拒绝访问"),
    NOT_FOUND(-404, "找不到资源"),
    CANNOT_ACCESS_DATABASE(-500, "连接不到数据库。"),
    NULL_POINTER_EXCEPTION(-500, "空指针异常。"),
    IO_EXCEPTION(-500, "IO异常。"),
    UNKNOWN_EXCEPTION(-500, "未明确的异常。"),
    SERVICE_EXCEPTION(-500, "服务异常。"),
    ILLEGAL_PARAMETER_EXCEPTION(-1000, "参数校验失败。");

    private final int code;
    private final String msg;

    ErrorCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int code() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    @Override
    public String toString() {
        return "ErrorCodeEnum{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
