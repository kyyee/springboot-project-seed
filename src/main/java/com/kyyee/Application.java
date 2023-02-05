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
        log.info("""
                Application '{}' is running! Access URLs:\n
                Local: \t\thttp://localhost:{}\n
                External: \t\thttp://{}:{}\n
                Doc: \t\thttp://{}:{}/doc.html\n
                Swagger-UI: \t\thttp://{}:{}/swagger-ui/index.html\n
                Actuator: \t\thttp://{}:{}{}\n
                """,
            environment.getProperty("spring.application.name"),
            environment.getProperty("server.port"),
            IpUtils.getLocalIP(),
            environment.getProperty("server.port"),
            IpUtils.getLocalIP(),
            environment.getProperty("server.port"),
            IpUtils.getLocalIP(),
            environment.getProperty("server.port"),
            IpUtils.getLocalIP(),
            environment.getProperty("server.port"),
            environment.getProperty("management.endpoint.web.base-path"));
    }
}
