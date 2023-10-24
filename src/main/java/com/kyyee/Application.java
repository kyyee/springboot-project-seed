/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee;

import com.kyyee.framework.common.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;

/**
 * @author kyyee
 */
@SpringBootApplication
@EnableTransactionManagement
@Slf4j
public class Application {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);
        String[] beans = context.getBeanDefinitionNames();
        Arrays.stream(beans).sorted().forEach(log::debug);
        Environment environment = context.getEnvironment();
        String serverPort = environment.getProperty("server.port");
        log.info("""
                Application '{}' is running! Access URLs:
                Local:        http://localhost:{}
                External:     http://{}:{}
                Doc:          http://{}:{}/doc.html
                Swagger-UI:   http://{}:{}/swagger-ui/index.html
                Actuator:     http://{}:{}{}
                """,
            environment.getProperty("spring.application.name"),
            serverPort,
            IpUtils.getLocalIP(),
            serverPort,
            IpUtils.getLocalIP(),
            serverPort,
            IpUtils.getLocalIP(),
            serverPort,
            IpUtils.getLocalIP(),
            serverPort,
            environment.getProperty("management.endpoint.web.base-path"));
    }
}
