/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.config;

import com.kyyee.sps.service.InitService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author kyyee
 */
@Component
public class StartupRunnerConfig implements ApplicationRunner {
    @Resource
    private
    InitService service;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        service.init();
    }
}
