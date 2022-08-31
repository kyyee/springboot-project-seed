package com.kyyee.framework.common.base;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Aspect
@Component
@Slf4j
public class PageAspect {

    public static final String PAGE = "execution(* com.kyyee..*.service.*.*(..)) && @annotation(com.kyyee.framework.common.base.AutoPage)";

    @Pointcut(PAGE) // 声明切点
    private void page() {
    }

    @Around("page()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        Optional<Object> optional = Arrays.stream(args).filter(o -> o.getClass().isAssignableFrom(PageQuery.class)).findAny();
        if (!optional.isPresent()) {
            return proceedingJoinPoint.proceed();
        }
        PageQuery pageParam = (PageQuery) optional.get();
        pageParam.dispose();
        Page<Object> page = PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize(), pageParam.orderBy());
        List<Object> objects = (List<Object>) proceedingJoinPoint.proceed();
        PageResult<List<Object>> result = PageResult.of(objects, page);
        log.info("分页列表查询结果：{}", result);
        return result;
    }
}
