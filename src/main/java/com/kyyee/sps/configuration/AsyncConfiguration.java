/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.configuration;

import com.kyyee.framework.common.exception.BaseException;
import com.kyyee.sps.common.constant.ConfigConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

/**
 * @author kyyee
 * springboot异步处理配置类
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfiguration extends AsyncConfigurerSupport {

    @Resource
    private TaskExecutorBuilder taskExecutorBuilder;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = taskExecutorBuilder.build();
        taskExecutor.setCorePoolSize(ConfigConst.CORE_POOL_SIZE);
        taskExecutor.setMaxPoolSize(ConfigConst.MAX_POOL_SIZE);
        taskExecutor.setQueueCapacity(ConfigConst.QUEUE_CAPACITY);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        super.getAsyncUncaughtExceptionHandler();
        return (throwable, method, objects) -> {
            if (throwable.getClass().isAssignableFrom(BaseException.class)) {
                String code = ((BaseException) throwable).getCode();
                String message = "错误码：" + code + "，原因：" + throwable.getMessage();
                log.info("message:{}", message);
            }
        };
    }
}
