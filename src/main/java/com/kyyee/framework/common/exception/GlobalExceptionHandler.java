package com.kyyee.framework.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.base.Joiner;
import com.kyyee.framework.common.base.Res;
import com.kyyee.framework.common.utils.SessionHelper;
import com.kyyee.framework.common.utils.SpringUtils;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler implements ThrowsAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<Object> constraintViolationException(ConstraintViolationException e) {
        log.error("ConstraintViolationException Params valid exception={}\n{}", SessionHelper.getRequest().getRequestURI(), e.getMessage());
        final Res<Object> res = Res.error(BaseErrorCode.INVALID_PARAM_ERROR.getCode(), e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
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
     * 请求方法不支持
     *
     * @param e
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<Object> methodNotSupportHandle(HttpRequestMethodNotSupportedException e) {
        log.error("HttpRequestMethodNotSupportedException exception={}", e.getMessage(), e);
        final Res<Object> res = Res.error(BaseErrorCode.HTTP_REQUEST_METHOD_NOT_SUPPORTED_ERROR.getCode(), e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> missingServletRequestParameterException(MissingServletRequestParameterException e) {
        Res<Object> result = Res.error(BaseErrorCode.INVALID_PARAM_ERROR.getCode(), BaseErrorCode.INVALID_PARAM_ERROR.getMsg() + e.getMessage());
        log.error("Params valid exception={}", e.getMessage(), e);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> maxUploadSizeExceededException(MaxUploadSizeExceededException e) {

        if (e.getCause().getCause() instanceof SizeLimitExceededException) {
            final SizeLimitExceededException slee = (SizeLimitExceededException) e.getCause().getCause();

            final String message = BaseErrorCode.FILE_SIZE_ERROR.getMsg() + "限制大小："
                + slee.getPermittedSize() / 1024 / 1024 + "MB，" + "实际大小：" + slee.getActualSize() / 1024 / 1024
                + "MB";
            Res<Object> result = Res.error(BaseErrorCode.FILE_SIZE_ERROR.getCode(), message);
            log.error("file size exceeded exception={}", e.getMessage(), e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        } else {
            Res<Object> result = Res.error(BaseErrorCode.FILE_SIZE_ERROR.getCode(), BaseErrorCode.FILE_SIZE_ERROR.getMsg() + e.getMessage());
            log.error("file size exceeded exception={}", e.getMessage(), e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(TypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<Object> typeMismatchHandle(TypeMismatchException e) {
        log.error("param type mismatch exception={}", e.getMessage(), e);
        final Res<Object> res = Res.error(BaseErrorCode.INVALID_PARAM_ERROR.getCode(), BaseErrorCode.INVALID_PARAM_ERROR.getMsg() + e.getPropertyName() + "类型应该为" + e.getRequiredType());
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<Object> httpMessageNotReadableHandle(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) e.getCause();
            Joiner joiner = Joiner.on(" ").skipNulls();
            String message = BaseErrorCode.INVALID_PARAM_ERROR.getMsg();
            if (null != ife) {
                message = joiner.join(message, "字段：", ife.getValue(), "正确类型:", ife.getTargetType());
            }

            Res<Object> result = Res.error(BaseErrorCode.INVALID_PARAM_ERROR.getCode(), message);
            log.error("param type mismatch exception={}", e.getMessage(), e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        } else {
            Res<Object> result = Res.error(BaseErrorCode.INVALID_PARAM_ERROR.getCode(), BaseErrorCode.INVALID_PARAM_ERROR.getMsg() + e.getMessage());
            log.error("param type mismatch exception={}", e.getMessage(), e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ResponseEntity<Object> noHandlerFoundException(NoHandlerFoundException e) {
        log.error("api not exist exception={}", e.getMessage(), e);
        final Res<Object> res = Res.error(BaseErrorCode.API_NOT_EXIST_ERROR.getCode(), BaseErrorCode.API_NOT_EXIST_ERROR.getMsg() + " 请求地址：" + e.getRequestURL());
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Object> multipartException(MultipartException e) {
        Res<Object> result = Res.error(BaseErrorCode.HTTP_REQUEST_FAILED.getCode(), BaseErrorCode.HTTP_REQUEST_FAILED.getMsg() + e.getMessage());
        log.error("upload file or form data exception={}", e.getMessage(), e);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

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
     * SQL 异常
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
     */
    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public ResponseEntity<Object> baseException(BaseException e) {
        log.error("internal server exception={}", e.getMessage(), e);
        final Res<Object> res = Res.error(e.getCode(), e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 全局异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Object> globalHandle(Exception e) {
        log.error("exception={}", e.getMessage(), e);
        final Res<Object> res = Res.of(BaseErrorCode.SYS_INTERNAL_ERROR);
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
