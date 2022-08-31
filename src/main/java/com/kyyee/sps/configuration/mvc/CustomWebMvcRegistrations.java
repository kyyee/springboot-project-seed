/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.configuration.mvc;

import com.kyyee.sps.common.component.annotation.ApiVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
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
//@Configuration
public class CustomWebMvcRegistrations implements WebMvcRegistrations {
    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new ApiVersionRequestMappingHandlerMapping();
    }

    @Getter
    @AllArgsConstructor
    class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {
        int version;

        @Override
        public ApiVersionCondition combine(ApiVersionCondition other) {
            return new ApiVersionCondition(other.getVersion());
        }

        @Override
        public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
            Matcher matcher = Pattern.compile("/v(\\d+).*").matcher(request.getRequestURI());
            if (matcher.find()) {
                int version = Integer.parseInt(matcher.group(1));
                if (version >= this.version) {
                    return this;
                }
            }
            return null;
        }

        @Override
        public int compareTo(ApiVersionCondition other, HttpServletRequest request) {
            return other.getVersion() - this.version;
        }
    }

    class ApiVersionRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
        @Override
        protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
            return createCondition(handlerType);
        }

        @Override
        protected RequestCondition<?> getCustomMethodCondition(Method method) {
            return createCondition(method.getClass());
        }

        private RequestCondition<ApiVersionCondition> createCondition(Class<?> clazz) {
            RequestMapping requestMapping = AnnotationUtils.findAnnotation(clazz, RequestMapping.class);
            if (requestMapping == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            if (requestMapping.value().length > 0) {
                sb.append(requestMapping.value()[0]);
            }
            String url = sb.toString();
            if (!url.contains("{version}")) {
                return null;
            }
            ApiVersion version = AnnotationUtils.findAnnotation(clazz, ApiVersion.class);
            return version == null ? new ApiVersionCondition(1) : new ApiVersionCondition(version.value());
        }
    }
}
