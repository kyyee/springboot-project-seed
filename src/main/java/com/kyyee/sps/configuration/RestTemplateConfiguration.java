/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;

/**
 * @author jm1138
 * rest 请求客户端配置
 */
@Slf4j
public class RestTemplateConfiguration implements RestTemplateCustomizer {

    @Value("${kyyee.restTemplate.connect.timeout:5000}")
    private int connectTimeout;
    @Value("${kyyee.restTemplate.read.timeout:20000}")
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        builder.setConnectTimeout(Duration.ofMillis(this.connectTimeout));
        builder.setReadTimeout(Duration.ofMillis(this.readTimeout));
        builder.setBufferRequestBody(false);
        return builder.build();
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                log.info("restTemplate hasError - {} - {}", clientHttpResponse.getStatusCode(), clientHttpResponse.getStatusCode().getReasonPhrase());
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
