/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.configuration.mvc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Component;

/**
 * @author kyyee
 * 认证授权配置
 */
@Component
@EnableWebSecurity
public class WebSecurityConfig implements WebSecurityConfigurer<WebSecurity> {
    @Value("${api-prefix:NA}")
    private String apiPrefix;

    @Override
    public void init(WebSecurity builder) throws Exception {

    }

    @Override
    public void configure(WebSecurity builder) throws Exception {
        builder.ignoring().requestMatchers(apiPrefix + "/**");
    }
}
