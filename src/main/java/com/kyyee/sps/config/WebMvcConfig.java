/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.config;

import com.kyyee.sps.interceptor.JwtInterceptor;
import com.kyyee.sps.interceptor.ProgramEnableInterceptor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author kyyee
 * 自定义拦截器
 * 自定义资源位置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();

        factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404.html"),
            new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html"));
        return factory;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ProgramEnableInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/hTest/**", "/hMock/**", "/error");
        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/hTest/**", "/hMock/**", "/error");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/json/**").addResourceLocations("classpath:/json/");
    }

}
