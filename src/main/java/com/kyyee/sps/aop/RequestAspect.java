/*
 * Copyright (c) 2017.  kyyee All rights reserved.
 */

package com.kyyee.sps.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
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

    public static final String GET = "@annotation(org.springframework.web.bind.annotation.GetMapping)";
    public static final String POST = "||@annotation(org.springframework.web.bind.annotation.PostMapping)";
    public static final String PUT = "||@annotation(org.springframework.web.bind.annotation.PutMapping)";
    public static final String PATCH = "||@annotation(org.springframework.web.bind.annotation.PatchMapping)";
    public static final String DELETE = "||@annotation(org.springframework.web.bind.annotation.DeleteMapping)";
    public static final String REQUEST = "||@annotation(org.springframework.web.bind.annotation.RequestMapping)";
    @Value("${request.aspect.excluded.urls:/swagger,}")
    private List<String> excludedUrls;

    @Pointcut(GET + POST + PUT + PATCH + DELETE + REQUEST) // 声明切点
    private void request() {
    }

    /**
     * 核心业务逻辑调用异常退出后，执行此advice，处理错误信息。
     *
     * @param proceedingJoinPoint 代理对象
     */
    @Around("request()") // 声明一个建言，传入定义的切点
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
