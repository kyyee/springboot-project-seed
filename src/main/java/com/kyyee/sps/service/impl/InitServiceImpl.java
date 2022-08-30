/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.service.impl;

import com.kyyee.sps.common.constant.KyyeeConfigProperties;
import com.kyyee.sps.service.InitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author kyyee
 */
@Service
@Slf4j
@EnableConfigurationProperties(KyyeeConfigProperties.class)
public class InitServiceImpl implements InitService {

    @Resource
    private
    KyyeeConfigProperties kyyeeConfigProperties;

    /**
     * 初始化成功标志
     */
    private static boolean init = false;

    /**
     * 初始化服务，查看数据表是否存在，不存在则创建
     */
    @Override
    public void init() {
        log.info("项目名称：{}（来源于application.yml配置文件）。", kyyeeConfigProperties.getProjectName());
        log.info("开发者：{}，联系方式：{}", kyyeeConfigProperties.getAuthor(), kyyeeConfigProperties.getEmail());
        initBaseConfig();
        initDatabase();
        setInit(true);
    }

    /**
     * 判断初始化是否完成
     *
     * @return 初始化完成返回true；否则返回false。
     */
    @Override
    public boolean isInit() {
        return init;
    }

    /**
     * 初始化基础配置
     */
    private void initBaseConfig() {
        // TODO 具体操作
        log.info("init base config success");
    }

    /**
     * 初始化数据库
     */
    private void initDatabase() {
        // TODO 具体操作
        log.info("init initDatabase success");
    }

    public static void setInit(boolean init) {
        InitServiceImpl.init = init;
    }
}
