/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.constant;

/**
 * @author kyyee
 */
public enum EmployeeDepartmentEnum {
    /**
     * 软件研究部
     */
    SOFTWARE_RESEARCH("软件研究部");

    private final String name;

    EmployeeDepartmentEnum(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "EmployeeDepartmentEnum{" +
                "name='" + name + '\'' +
                '}';
    }
}
