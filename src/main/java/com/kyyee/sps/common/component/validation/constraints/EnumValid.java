package com.kyyee.sps.common.component.validation.constraints;

import com.kyyee.sps.common.component.validation.EnumValidValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumValidValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValid {

    String message() default "{com.kyyee.sps.common.component.validation.constraints.EnumValid.message}";

    /**
     * 校验的枚举必须实现 method() 方法
     */
    Class<?> target();

    /**
     * 默认需实现validField方法
     */
    String method() default "validField";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] notAllowScope() default {};
}
