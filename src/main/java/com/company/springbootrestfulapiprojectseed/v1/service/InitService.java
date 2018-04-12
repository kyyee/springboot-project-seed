/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.v1.service;

/**
 * @author kyyee
 * 服务初始化
 */
public interface InitService {
    /**
     * 初始化服务，查看数据表是否存在，不存在则创建
     */
    void init();

    /**
     * 判断初始化是否完成
     *
     * @return 初始化完成返回true；否则返回false。
     */
    boolean isInit();
}
