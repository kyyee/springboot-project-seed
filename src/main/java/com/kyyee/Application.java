/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author kyyee
 */
@SpringBootApplication
@Slf4j
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
//                .getBean(InitService.class).init();

    }
}
