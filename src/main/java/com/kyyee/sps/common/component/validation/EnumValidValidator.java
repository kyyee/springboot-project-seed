package com.kyyee.sps.common.component.validation;

import com.kyyee.sps.common.component.validation.constraints.EnumValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class EnumValidValidator implements ConstraintValidator<EnumValid, String> {

    Class<?> clazz;

    String method;

    @Override
    public void initialize(EnumValid constraintAnnotation) {
        clazz = constraintAnnotation.target();
        method = constraintAnnotation.method();
    }

    @Override
    public boolean isValid(String val, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isEmpty(val)) {
            return false;
        }
        if (Objects.nonNull(clazz)) {
            if (clazz.isEnum()) {
                Method invokeMethod;
                try {
                    invokeMethod = clazz.getMethod(this.method);
                } catch (NoSuchMethodException e) {
                    return false;
                }
                for (Object object : clazz.getEnumConstants()) {
                    Object value;
                    try {
                        value = invokeMethod.invoke(object);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        return false;
                    }
                    if (Objects.nonNull(value)) {
                        if (val.equals(value.toString())) {
                            return true;
                        }
                    }
                }
            }
        } else {
            return true;
        }
        return false;
    }
}
