/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.exception;

import com.kyyee.sps.common.HttpResponseUtil;
import com.kyyee.sps.constant.ErrorCodeEnum;
import com.kyyee.sps.dto.StandardResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

/**
 * @author kyyee
 * 异常处理类
 */
@ControllerAdvice
public class ExceptionsHandler implements ThrowsAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionsHandler.class);

    @ExceptionHandler(DataAccessResourceFailureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public StandardResponseDTO dataAccessResourceFailureException(DataAccessResourceFailureException e) {
        LOGGER.error(e.getMessage());
        return HttpResponseUtil.error(ErrorCodeEnum.CANNOT_ACCESS_DATABASE);
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public StandardResponseDTO nullPointerException(NullPointerException e) {
        LOGGER.error(e.getMessage());
        return HttpResponseUtil.error(ErrorCodeEnum.NULL_POINTER_EXCEPTION);
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public StandardResponseDTO ioException(IOException e) {
        LOGGER.error(e.getMessage());
        return HttpResponseUtil.error(ErrorCodeEnum.IO_EXCEPTION);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public StandardResponseDTO exception(Exception e) {
        LOGGER.error("{}, message: {}", e.getClass(), e.getMessage());
        return HttpResponseUtil.error(ErrorCodeEnum.UNKNOWN_EXCEPTION);
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public StandardResponseDTO serviceException(ServiceException e) {
        LOGGER.error(e.getMessage());
        return HttpResponseUtil.error(ErrorCodeEnum.SERVICE_EXCEPTION, e.getMessage());
    }

    @ExceptionHandler(IllegalParameterException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public StandardResponseDTO illegalParameterException(IllegalParameterException e) {
        LOGGER.error(e.getMessage());
        return HttpResponseUtil.error(ErrorCodeEnum.ILLEGAL_PARAMETER_EXCEPTION);
    }
}
