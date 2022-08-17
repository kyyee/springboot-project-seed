/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.config;

import com.kyyee.sps.constant.ConfigConst;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author kyyee
 * springboot异步处理配置类
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(ConfigConst.CORE_POOL_SIZE);
        taskExecutor.setMaxPoolSize(ConfigConst.MAX_POOL_SIZE);
        taskExecutor.setQueueCapacity(ConfigConst.QUEUE_CAPACITY);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
