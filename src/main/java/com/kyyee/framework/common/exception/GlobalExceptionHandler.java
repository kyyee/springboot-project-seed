package com.kyyee.framework.common.exception;

/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyyee.framework.common.base.Res;
import com.kyyee.framework.common.utils.SessionHelper;
import com.kyyee.framework.common.utils.SpringUtils;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.List;

/**
 * description
 *
 * @author t17153 [tan.gang@unisinsight.com]
 * @date 2018/12/04 15:03
 * @since 1.0
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(FeignException.class)
    @ResponseBody
    public ResponseEntity<Object> feignException(FeignException e) {
        final String content = e.contentUTF8();
        Object data = null;
        Res<Object> res = null;
        if (!StringUtils.isEmpty(content)) {
            final ObjectMapper objectMapper = SpringUtils.getBean(ObjectMapper.class);
            try {
                TypeReference<Res<Object>> typeReference = new TypeReference<Res<Object>>() {
                };
                res = objectMapper.readValue(content, typeReference);
                if (res.isSuccess()) {
                    data = res.getData();
                }
            } catch (JsonProcessingException jsonProcessingException) {
                jsonProcessingException.printStackTrace();
            }
        }

        if (res == null || res.isSuccess()) {
            res = Res.error(BaseErrorCode.CALL_FAILED.of());
            res.setData(data);
        }

        log.error("Remote call exception={}\n{}", e.request().url(), res);
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 参数校验异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Object> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Params valid exception={}\n{}", SessionHelper.getRequest().getRequestURI(), e.getMessage());
        return bindingResult(e.getBindingResult());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<Object> constraintViolationException(ConstraintViolationException e) {
        log.error("ConstraintViolationException Params valid exception={}\n{}", SessionHelper.getRequest().getRequestURI(), e.getMessage());
        final Res<Object> res = Res.error(BaseErrorCode.INVALID_PARAM_ERROR.getCode(), e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity<Object> bindException(BindException e) {
        log.error("Params valid exception={}\n{}", SessionHelper.getRequest().getRequestURI(), e.getMessage());
        return bindingResult(e.getBindingResult());
    }

    private ResponseEntity<Object> bindingResult(BindingResult bindingResult) {
        final String notEmpty = "不能为空";
        List<FieldError> errors = bindingResult.getFieldErrors();
        StringBuilder messageBuilder = new StringBuilder();
        String message;
        for (int i = 0; i < errors.size(); i++) {
            FieldError error = errors.get(i);
            message = Strings.isNotBlank(error.getDefaultMessage()) ? error.getDefaultMessage() : notEmpty;
            if (notEmpty.equals(message)) {
                messageBuilder.append(error.getField());
            }
            messageBuilder.append(message);
            if (i < errors.size() - 1) {
                messageBuilder.append(";");
            }
        }
        final Res<Object> res = Res.error(BaseErrorCode.INVALID_PARAM_ERROR.getCode(), messageBuilder.toString());
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    /**
     * SQL 异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(SQLException.class)
    @ResponseBody
    public ResponseEntity<Object> SQLException(SQLException e) {
        log.error("sql  exception={}", e.getMessage(), e);
        final Res<Object> res = Res.of(BaseErrorCode.SQL_EXCEPTION);
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public ResponseEntity<Object> baseException(BaseException e) {
        log.error("internal server exception={}", e.getMessage(), e);
        final Res<Object> res = Res.error(e.getCode(), e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 请求方法不支持
     *
     * @param e
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<Object> methodNotSupportHandle(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException exception={}", e.getMessage(), e);
        final Res<Object> res = Res.error(BaseErrorCode.HTTP_REQUEST_FAILED.getCode(), e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * 参数类型不匹配
     *
     * @param e
     * @return
     */
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<Object> typeMismatchHandle(TypeMismatchException e) {
        log.error("param type mismatch exception={}", e.getMessage(), e);
        final Res<Object> res = Res.error(BaseErrorCode.INVALID_PARAM_ERROR.getCode(), BaseErrorCode.INVALID_PARAM_ERROR.getMsg() + e.getPropertyName() + "类型应该为" + e.getRequiredType());
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    /**
     * 参数类型不匹配
     *
     * @param e
     * @return
     */
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<Object> httpMessageNotReadableHandle(HttpMessageNotReadableException e) {
//        log.error("param type mismatch exception={}", e.getMessage(), e);
//        InvalidFormatException ife = (InvalidFormatException) e.getCause();
//        Joiner joiner = Joiner.on(" ").skipNulls();
//        String message = BaseErrorCode.INVALID_PARAM_ERROR.getMsg();
//        if (null != ife) {
//            message = joiner.join(message, "字段：", ife.getValue(), "正确类型:", ife.getTargetType());
//        }
//        final Res<Object> result = Res.error(BaseErrorCode.INVALID_PARAM_ERROR.getCode(), message);
//        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
//    }

    /**
     * 请求地址不存在
     *
     * @param e
     * @return
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ResponseEntity<Object> noHandlerFoundException(NoHandlerFoundException e) {
        log.error("api not exist exception={}", e.getMessage(), e);
        final Res<Object> res = Res.error(BaseErrorCode.API_NOT_EXIST_ERROR.getCode(), BaseErrorCode.API_NOT_EXIST_ERROR.getMsg() + " 请求地址：" + e.getRequestURL());
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    /**
     * 全局异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Object> globalHandle(Exception e) {
        log.error("exception={}", e.getMessage(), e);
        final Res<Object> res = Res.of(BaseErrorCode.SYS_INTERNAL_ERROR);
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
