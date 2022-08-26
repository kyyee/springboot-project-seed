package com.kyyee.framework.common.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在@Service需要分页的方法加上此注解，可实现自动分页，并自动包装为PageResult对象
 * 注意事项：
 * 1.mapper方法的返回结果类型必须为 Object
 * 2.service方法的返回结果类型必须为 Object
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoPage {
    Class<?> dto() default Object.class;
}
