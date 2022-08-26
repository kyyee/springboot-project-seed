package com.kyyee.framework.common.interceptor;
/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */

import com.kyyee.framework.common.constant.GlobalConstant;
import com.kyyee.framework.common.utils.ThreadLocalUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author yanglei 00699 [yang.lei@unisinsight.com]
 * @date 2020/11/09 11:29
 * @since 1.0
 */
public class FeignClientRestHeaderInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String user = ThreadLocalUtils.get(GlobalConstant.USER);
        if (StringUtils.hasText(user)) {
            requestTemplate.header(GlobalConstant.USER, user);
        }
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(requestAttributes)) {
            HttpServletRequest request = requestAttributes.getRequest();
            if (!ObjectUtils.isEmpty(request)) {
                String outer = request.getHeader("outer");
                Cookie[] cookies = request.getCookies();
                if (!ObjectUtils.isEmpty(cookies)) {
                    for (Cookie cookie : cookies) {
                        if ("outer".equals(cookie.getName())) {
                            outer = cookie.getValue();
                            break;
                        }
                    }
                }
                if (StringUtils.hasText(outer)) {
                    requestTemplate.header("outer", outer);
                }
            }
        }
    }
}
