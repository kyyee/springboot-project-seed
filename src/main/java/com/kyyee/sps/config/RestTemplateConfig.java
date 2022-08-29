/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * @author jm1138
 * rest 请求客户端配置
 */
@Configuration
public class RestTemplateConfig implements RestTemplateCustomizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateConfig.class);

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                LOGGER.info("restTemplate hasError - {} - {}", clientHttpResponse.getStatusCode(), clientHttpResponse.getStatusCode().getReasonPhrase());
                return clientHttpResponse.getStatusCode().is4xxClientError()
                        || clientHttpResponse.getStatusCode().is5xxServerError();
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                throw new IOException(clientHttpResponse.getStatusCode().getReasonPhrase());
            }
        });
    }
}
