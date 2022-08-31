/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.common.component.interceptor;

import com.kyyee.sps.common.constant.ConfigConst;
import com.kyyee.sps.service.InitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * @author kyyee
 * 初始化程序，如从Excel读取数据写入数据库等。
 */
@Slf4j
@Component
public class ProgramEnableInterceptor implements HandlerInterceptor {

    @Resource
    private InitService service;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!service.isInit()) {
            log.warn("{} ： 请求因程序初始化未完成而拒绝响应", request.getRequestURL());

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setHeader(ConfigConst.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE);
            response.getOutputStream()
                .write(HttpStatus.FORBIDDEN.getReasonPhrase()
                    .getBytes(StandardCharsets.UTF_8));
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
