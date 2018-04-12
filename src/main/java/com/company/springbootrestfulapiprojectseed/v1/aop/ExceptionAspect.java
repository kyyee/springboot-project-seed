/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.v1.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author kyyee
 * 异常拦截切面
 */
@Aspect
@Component
public class ExceptionAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAspect.class);

    private static final String REQUEST_MAPPING = "@annotation(org.springframework.web.bind.annotation.RequestMapping)";

    /**
     * 声明切点
     */
    @Pointcut(REQUEST_MAPPING)
    private void annotationPointCut() {
    }

    /**
     * 在核心业务执行前执行，不能阻止核心业务的调用。
     *
     * @param joinPoint 代理对象
     */
    @Before("annotationPointCut()")
    public void doBefore(JoinPoint joinPoint) {
        LOGGER.info("method: {}.{}() start ...", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
    }

    /**
     * 在核心业务退出后执行（含正常执行结束和异常退出）。
     *
     * @param joinPoint 代理对象
     */
    @After("annotationPointCut()")
    public void doAfter(JoinPoint joinPoint) {
        LOGGER.info("method: {}.{}() end ...", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
    }

    /**
     * 核心业务逻辑调用异常退出后，执行此advice，处理错误信息。
     *
     * @param proceedingJoinPoint 代理对象
     * @return 错误信息
     * @throws Throwable 错误对象
     */
    @Around("annotationPointCut()") // 声明一个建言，传入定义的切点
    public Object handleThrowing(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return proceedingJoinPoint.proceed();
    }

    /**
     * @param object response返回对象
     * @return response返回对象的字符串
     */
    @AfterReturning(pointcut = "annotationPointCut()", returning = "object")
    public Object doAfterReturning(Object object) {
        LOGGER.info("response :{}", object.toString());
        return object;
    }
}
