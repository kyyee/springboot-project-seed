/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.v1.config;

import com.company.springbootrestfulapiprojectseed.v1.interceptor.JwtInterceptor;
import com.company.springbootrestfulapiprojectseed.v1.interceptor.ProgramEnableInterceptor;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author kyyee
 * 自定义拦截器
 * 自定义资源位置
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Resource
    private
    ProgramEnableInterceptor programEnableInterceptor;

    @Resource
    private
    JwtInterceptor jwtInterceptor;

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404.html"));
        factory.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html"));
        factory.setSessionTimeout(30, TimeUnit.MINUTES);
        return factory;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(programEnableInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/hTest/**", "/hMock/**", "/error");
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/hTest/**", "/hMock/**", "/error");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/json/**").addResourceLocations("classpath:/json/");
    }

}
