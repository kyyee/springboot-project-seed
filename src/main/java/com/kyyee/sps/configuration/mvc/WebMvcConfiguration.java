/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.configuration.mvc;

import com.kyyee.framework.common.config.SnakeToCamelModelAttributeMethodProcessor;
import com.kyyee.framework.common.interceptor.BaseHeaderInterceptor;
import com.kyyee.sps.common.component.converter.LocalDateConverter;
import com.kyyee.sps.common.component.converter.LocalDateTimeConverter;
import com.kyyee.sps.common.component.converter.LocalTimeConverter;
import com.kyyee.sps.common.component.interceptor.ProgramEnableInterceptor;
import com.kyyee.sps.common.utils.JSON;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author kyyee
 * 自定义拦截器
 * 自定义资源位置
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Value("${api-prefix:NA}")
    private String apiPrefix;

    @Value("${kyyee.file.save-dir:/home/file/}")
    private String saveDir;

    @Resource
    private ProgramEnableInterceptor programEnableInterceptor;

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();

        factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404.html"),
            new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html"));
        return factory;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SnakeToCamelModelAttributeMethodProcessor(true));
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new LocalDateTimeConverter());
        registry.addConverter(new LocalDateConverter());
        registry.addConverter(new LocalTimeConverter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(programEnableInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns("/swagger-ui/**", "/file.html", "/error");
        registry.addInterceptor(new BaseHeaderInterceptor())
            .addPathPatterns("/**")
            .excludePathPatterns("/swagger-ui/**", "/file.html", "/error");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/static/favicon.ico");

        registry.addResourceHandler(apiPrefix + "/fs/**").addResourceLocations("file:" + saveDir);

        registry.addResourceHandler(apiPrefix + "/images/**").addResourceLocations("classpath:/images/");
        registry.addResourceHandler(apiPrefix + "/audios/**").addResourceLocations("classpath:/audios/");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(JSON.mapper());

        converters.add(converter);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
            .allowCredentials(true)
            .allowedOrigins("*")
            .allowedHeaders("*")
            .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS")
            .maxAge(3600L);
    }
}
