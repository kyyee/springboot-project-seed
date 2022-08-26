package com.kyyee.framework.common.interceptor;

/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */

import com.kyyee.framework.common.constant.GlobalConstant;
import com.kyyee.framework.common.interceptor.user.User;
import com.kyyee.framework.common.interceptor.user.UserHandler;
import com.kyyee.framework.common.utils.ThreadLocalUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;

/**
 * description
 *
 * @author t17153 [tan.gang@h3c.com]
 * @date 2018/10/22 18:10
 * @since 1.0
 */
public class BaseHeaderInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String user = request.getHeader(GlobalConstant.USER);
        if (StringUtils.hasText(user)) {
            ThreadLocalUtils.put(GlobalConstant.USER, user);
            //user 解析
            handleUserInfo(user);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        ThreadLocalUtils.remove(GlobalConstant.USER);
        UserHandler.remove();
    }

    private void handleUserInfo(String authorization) throws Exception {
        //编码转换
//        authorization = EscapeUtils.unescape(EscapeUtils.unescape(authorization));
        authorization = URLDecoder.decode(authorization, "UTF-8");

        String[] array = authorization.split("&");

        User user = UserHandler.getUser();
        if (null == user) {
            user = new User();
        }
        for (String line : array) {
            String[] keyValue = line.split(":");
            if (keyValue.length < 2) {
                continue;
            }
            if ("usercode".equalsIgnoreCase(keyValue[0])) {
                user.setUserCode(keyValue[1]);
            }

            if ("username".equalsIgnoreCase(keyValue[0])) {
                user.setUserName(keyValue[1]);
            }
        }

        //处理其它用户信息
        UserHandler.setUser(user);
    }
}
