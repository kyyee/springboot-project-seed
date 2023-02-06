package com.kyyee.sps.common.utils;

import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

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
