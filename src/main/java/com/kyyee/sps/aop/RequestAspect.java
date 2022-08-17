/*
 * Copyright (c) 2017.  kyyee All rights reserved.
 */

package com.kyyee.sps.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jm1138 on 2017/7/25.
 * 异常拦截切面
 */
@Aspect // 声明切面
@Component // 让此切面成为Spring容器管理的bean
@Slf4j
public class RequestAspect {

    private static final String CUT = "@annotation(org.springframework.web.bind.annotation.RestController)";

    @Value("${request.aspect.excluded.urls:,}")
    private List<String> excludedUrls;

    @Pointcut(CUT) // 声明切点
    private void requestLog() {
    }

    /**
     * 在核心业务执行前执行，不能阻止核心业务的调用。
     *
     * @param joinPoint 代理对象
     */
    @Before("requestLog()")
    public void doBefore(JoinPoint joinPoint) {
    }

    /**
     * 在核心业务退出后执行（含正常执行结束和异常退出）。
     *
     * @param joinPoint 代理对象
     */
    @After("requestLog()")
    public void doAfter(JoinPoint joinPoint) {
    }

    /**
     * 核心业务逻辑调用异常退出后，执行此advice，处理错误信息。
     *
     * @param proceedingJoinPoint 代理对象
     */
    @Around("requestLog()") // 声明一个建言，传入定义的切点
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String requestURI = request.getRequestURI();

        if (!excludedUrls.contains(requestURI)) {
            log.info("REQUEST {} : {}", requestURI, Arrays.toString(proceedingJoinPoint.getArgs()));
        }
        try {
            Object proceed = proceedingJoinPoint.proceed();
            if (!excludedUrls.contains(requestURI)) {
                log.info("RESPONSE : {}", proceed);
            }
            return proceed;
        } catch (Throwable e) {
            log.error("REQUEST {} : {}", requestURI, Arrays.toString(proceedingJoinPoint.getArgs()), e);
            throw e;
        }
    }

}
