package com.kyyee.framework.common.base;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.IErrorCode;
import lombok.Data;

/**
 * 返回结果类
 */
@Data
public final class Res<T> {
    private static final String SUCCESS_CODE = "0000000000";
    private String code = SUCCESS_CODE;

    private String msg = "请求成功";

    private T data;


    private Res() {
    }

    private Res(T data) {
        this.data = data;
    }

    private Res(String code, String msg) {
        this(code, msg, null);
    }

    private Res(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Res<Object> success() {
        return new Res<>();
    }

    public static <T> Res<T> success(T data) {
        return new Res<>(data);
    }

    public static <T> Res<T> success(T data, String msg) {
        return new Res<>(SUCCESS_CODE, msg, data);
    }

    public static Res<Object> error(String code, String msg) {
        return new Res<>(code, msg);
    }

    public static Res<Object> error(IErrorCode error) {
        return new Res<>(error.getCode(), error.getMsg());
    }

    public static <T> Res<T> of(String code, String msg, T data) {
        return new Res<>(code, msg, data);
    }

    public static Res<Object> of(BaseErrorCode baseErrorCode) {
        return new Res<>(baseErrorCode.getCode(), baseErrorCode.getMsg());
    }

    @JsonIgnore
    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }
}
