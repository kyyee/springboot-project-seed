/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.v1.scheduler;

import com.company.springbootrestfulapiprojectseed.v1.constant.ConfigConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author kyyee
 * 所有定时器任务统一定义在此类
 */
@Component
public class Tasks {
    private static final Logger LOGGER = LoggerFactory.getLogger(Tasks.class);

    /**
     * 计划任务，每隔5分钟更新一次数据库VM的运行状态
     */
    @Scheduled(fixedRate = ConfigConst.FIXED_RATE_THRESHOLD)
    public void scheduled() {
        LOGGER.info("5 分钟一次轮询！");
    }
}
