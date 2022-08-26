/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.mapper.primary;

import com.kyyee.sps.model.primary.Employee;
import io.mybatis.mapper.Mapper;

/**
 * @author kyyee
 */
@org.apache.ibatis.annotations.Mapper
public interface EmployeeMapper extends Mapper<Employee, Long> {
    /**
     * 根据雇员姓名获取某一雇员信息
     *
     * @param name 姓名，不能重复
     * @return Employee
     */
    Employee findByName(String name);
}
