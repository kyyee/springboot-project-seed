/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed;

import com.company.springbootrestfulapiprojectseed.service.InitService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author kyyee
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args)
                .getBean(InitService.class).init();
    }
}
