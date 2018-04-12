/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.v1.config;

import com.company.springbootrestfulapiprojectseed.v1.annotation.RestApiVersion;
import org.springframework.boot.autoconfigure.web.WebMvcRegistrationsAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kyyee
 * restful api 版本管理
 */
@Configuration
public class WebMvcRegistrationsConfig extends WebMvcRegistrationsAdapter {
    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new ApiRequestMappingHandlerMapping();
    }

    static class RestApiVersionCondition implements RequestCondition<RestApiVersionCondition> {
        private final static Pattern VERSION_PREFIX_PATTERN = Pattern.compile("/v(\\d+).*");

        int restApiVersion;

        RestApiVersionCondition(int restApiVersion) {
            this.restApiVersion = restApiVersion;
        }

        private int getRestApiVersion() {
            return restApiVersion;
        }

        @Override
        public RestApiVersionCondition combine(RestApiVersionCondition restApiVersionCondition) {
            return new RestApiVersionCondition(restApiVersionCondition.getRestApiVersion());
        }

        @Override
        public RestApiVersionCondition getMatchingCondition(HttpServletRequest httpServletRequest) {
            Matcher matcher = VERSION_PREFIX_PATTERN.matcher(httpServletRequest.getRequestURI());
            if (matcher.find()) {
                int version = Integer.valueOf(matcher.group(1));
                if (version >= this.restApiVersion) {
                    return this;
                }
            }
            return null;
        }

        @Override
        public int compareTo(RestApiVersionCondition restApiVersionCondition, HttpServletRequest httpServletRequest) {
            return restApiVersionCondition.getRestApiVersion() - this.restApiVersion;
        }
    }

    class ApiRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
        @Override
        protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
            return createCondition(handlerType);
        }

        @Override
        protected RequestCondition<?> getCustomMethodCondition(Method method) {
            return createCondition(method.getClass());
        }

        private RequestCondition<RestApiVersionCondition> createCondition(Class<?> clazz) {
            RequestMapping classRequestMapping = AnnotationUtils.findAnnotation(clazz, RequestMapping.class);
            if (classRequestMapping == null) {
                return null;
            }
            StringBuilder mappingUrlBuilder = new StringBuilder();
            if (classRequestMapping.value().length > 0) {
                mappingUrlBuilder.append(classRequestMapping.value()[0]);
            }
            String mappingUrl = mappingUrlBuilder.toString();
            if (!mappingUrl.contains("{version}")) {
                return null;
            }
            RestApiVersion apiVersion = AnnotationUtils.findAnnotation(clazz, RestApiVersion.class);
            return apiVersion == null ? new RestApiVersionCondition(1) : new RestApiVersionCondition(apiVersion.value());
        }
    }
}
