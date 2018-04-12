/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.v1.service;

import com.company.springbootrestfulapiprojectseed.v1.domain.EmployeeDO;

import java.util.List;

/**
 * @author kyyee
 */
public interface EmployeeService {

    /**
     * 根据雇员姓名获取某一雇员信息
     *
     * @param name 雇员姓名
     * @return EmployeeDO
     */
    EmployeeDO getEmployeeByName(String name);

    /**
     * 获取全部雇员信息
     *
     * @return List<EmployeeDO>
     */
    List<EmployeeDO> listEmployee();

    /**
     * 统计雇员人数
     *
     * @return long
     */
    long countEmployee();

    /**
     * 新增雇员信息，框架会为雇员增加id，创建时间，修改时间
     *
     * @param employee 雇员基本信息
     * @return EmployeeDO
     */
    EmployeeDO saveEmployee(EmployeeDO employee);

    /**
     * 删除雇员信息
     *
     * @param name 雇员姓名
     */
    void removeEmployee(String name);

    /**
     * 更新雇员信息
     *
     * @param name     雇员姓名
     * @param employee 雇员基本信息
     * @return EmployeeDO
     */
    EmployeeDO updateEmployee(String name, EmployeeDO employee);
}
