/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.common.component.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author kyyee
 * 所有定时器任务统一定义在此类
 */
@Component
@Slf4j
public class Tasks {

    /**
     * 计划任务，每隔5分钟更新一次数据库VM的运行状态
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void scheduled() {
        log.info("5 分钟一次轮询！");
    }
}
