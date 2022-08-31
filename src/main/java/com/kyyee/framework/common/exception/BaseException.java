package com.kyyee.framework.common.exception;


import org.slf4j.helpers.MessageFormatter;

/**
 * 基础异常
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = -1696147473539565567L;
    protected String code;

    public BaseException(String message) {
        super(message);
    }

    public static BaseException of(IErrorCode code) {
        BaseException exception = new BaseException(code.getMsg());
        exception.code = code.getCode();
        return exception;
    }

    public static BaseException of(IErrorCode code, String message) {
        BaseException exception = new BaseException(message);
        exception.code = code.getCode();
        return exception;
    }

    public static BaseException of(BaseErrorCode code) {
        return of(code.of());
    }

    public static BaseException of(BaseErrorCode code, String message) {
        BaseException exception = new BaseException(message);
        exception.code = code.getCode();

        return exception;
    }

    public static BaseException of(IErrorCode code, String message, Object... values) {
        return of(code, replace(message, values));
    }


    public static BaseException of(String code, String message, Object... values) {
        BaseException exception = new BaseException(replace(message, values));
        exception.code = code;
        return exception;
    }

    private static String replace(String message, Object... values) {
        if (message == null) {
            return "";
        }
        if (values != null) {
            return MessageFormatter.arrayFormat(message, values).getMessage();
        } else {
            return message;
        }
    }

    public String getCode() {
        return code;
    }
}
