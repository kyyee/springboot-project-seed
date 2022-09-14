package com.kyyee.sps.common.utils;

import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

@Slf4j
public class EasyExcelUtils {

    private EasyExcelUtils() {
    }

    public static <T> String validate(T object, int rowIndex) {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();
            Set<ConstraintViolation<Object>> violations = validator.validate(object);
            if (ObjectUtils.isEmpty(violations)) {
                return "";
            }
            StringBuilder message = new StringBuilder();
            for (ConstraintViolation<Object> violation : violations) {
                message.append(violation.getMessage()).append(" ");
            }
            log.error("第{}行==>{}", rowIndex, message);
            throw BaseException.of(BaseErrorCode.EXCEL_IMPORT_FAILED.of(), "第{}行，{}", rowIndex, message);
        }
    }
}
