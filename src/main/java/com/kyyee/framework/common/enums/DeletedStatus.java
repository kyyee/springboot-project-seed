package com.kyyee.framework.common.enums;


/**
 * 伪删常量
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
