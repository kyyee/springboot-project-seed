package com.kyyee.sps.common.exception;

import com.kyyee.framework.common.base.Res;
import com.kyyee.framework.common.exception.BaseErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.UnknownHostException;

@ControllerAdvice
@Slf4j
public class CustomExceptionsHandler implements ThrowsAdvice {

    public static final String DATABASE_NOT_EXIST = "[\\s\\S]*FATAL: database [\\s\\S]* does not exist[\\s\\S]*";

    // todo To generate documentation automatically, make sure all the methods declare the HTTP Code responses using the annotation: @ResponseStatus
    @ExceptionHandler(UnknownHostException.class)
    public ResponseEntity<Object> unknownHostException(UnknownHostException e) {
        Res<Object> result = Res.error(BaseErrorCode.HTTP_REQUEST_FAILED.getCode(), BaseErrorCode.HTTP_REQUEST_FAILED.getMsg() + e.getMessage());
        log.error("unknown host exception={}", e.getMessage(), e);
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler({PSQLException.class})
    public ResponseEntity<Object> psqlException(PSQLException e) {
        Res<Object> result = Res.error(BaseErrorCode.SQL_EXCEPTION.getCode(), BaseErrorCode.SQL_EXCEPTION.getMsg());
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (e.getMessage().matches(DATABASE_NOT_EXIST)) {
            result = Res.error(BaseErrorCode.SQL_EXCEPTION.getCode(), "数据库不存在，请检查");
            status = HttpStatus.BAD_REQUEST;
        }
        log.error("sql exception={}", e.getMessage(), e);
        return new ResponseEntity<>(result, status);
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<Object> dataAccessResourceFailureException(DataAccessResourceFailureException e) {
        Res<Object> result = Res.error(BaseErrorCode.DATABASE_ERROR.getCode(), BaseErrorCode.DATABASE_ERROR.getMsg() + e.getMessage());
        log.error("data access resource failure exception={}", e.getMessage(), e);
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
