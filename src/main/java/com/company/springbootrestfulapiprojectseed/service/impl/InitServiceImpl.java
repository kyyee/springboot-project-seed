/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.service.impl;

import com.company.springbootrestfulapiprojectseed.constant.ConfigInjection;
import com.company.springbootrestfulapiprojectseed.service.InitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author kyyee
 */
@Service
public class InitServiceImpl implements InitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitServiceImpl.class);

    @Resource
    private
    ConfigInjection configInjection;

    // 初始化成功标志
    private static boolean init = false;

    /**
     * 初始化服务，查看数据表是否存在，不存在则创建
     */
    @Override
    public void init() {
        LOGGER.info("项目名称：{}（来源于application.yml配置文件）。", configInjection.getProjectName());
        LOGGER.info("开发者：{}，联系方式：{}", configInjection.getAuthor(), configInjection.getEmail());
        initNode();
        initVm();
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
     * 初始化节点表
     */
    private void initNode() {
        LOGGER.info("init node success");
    }

    /**
     * 删除无用虚拟机
     */
    private void initVm() {
        LOGGER.info("init virtual machine success");
    }

    public static void setInit(boolean init) {
        InitServiceImpl.init = init;
    }
}
