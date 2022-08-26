/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.service;

import com.kyyee.framework.common.base.AutoPage;
import com.kyyee.framework.common.base.PageQuery;
import com.kyyee.sps.model.primary.Employee;

/**
 * @author kyyee
 */
public interface EmployeeService {

    /**
     * 根据雇员姓名获取某一雇员信息
     *
     * @param name 雇员姓名
     * @return Employee
     */
    Employee getEmployeeByName(String name);

    /**
     * 获取全部雇员信息
     *
     * @return List<Employee>
     */
    @AutoPage
    Object listEmployee(PageQuery pageQuery);

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
     * @return Employee
     */
    Employee saveEmployee(Employee employee);

    /**
     * 删除雇员信息
     *
     * @param name 雇员姓名
     */
    void removeEmployee(Long name);

    /**
     * 更新雇员信息
     *
     * @param id       雇员姓名
     * @param employee 雇员基本信息
     * @return Employee
     */
    Employee updateEmployee(Long id, Employee employee);
}
