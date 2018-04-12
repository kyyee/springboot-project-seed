/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.v1.interceptor;

import com.company.springbootrestfulapiprojectseed.v1.constant.ConfigConst;
import com.company.springbootrestfulapiprojectseed.v1.service.InitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kyyee
 * 初始化程序，如从Excel读取数据写入数据库等。
 */
@Component
public class ProgramEnableInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramEnableInterceptor.class);

    @Resource
    private
    InitService service;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if (!service.isInit()) {
            LOGGER.warn("{} ： 请求因程序初始化未完成而拒绝响应", httpServletRequest.getRequestURL());

            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpServletResponse.setHeader(ConfigConst.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE);
            httpServletResponse.getOutputStream()
                    .write(HttpStatus.FORBIDDEN.getReasonPhrase()
                            .getBytes(ConfigConst.CHARSET_UTF8));
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}
