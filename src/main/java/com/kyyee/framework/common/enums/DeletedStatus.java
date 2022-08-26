package com.kyyee.framework.common.enums;

/*
 * www.unisinsight.com Inc.
 * Copyright (c) 2018 All Rights Reserved
 */

/**
 * description 伪删常量
 *
 * @author liuran [KF.liuran@h3c.com]
 * @date 2018/9/6 17:12
 * @since 1.0
 */
public enum DeletedStatus {
    /**
     * 已删除为DELETED，存在为EXIST
     */
    DELETED((short) -1), EXIST((short) 0);

    private final Short status;

    DeletedStatus(Short status) {
        this.status = status;
    }

    public Short value() {
        return status;
    }
}
