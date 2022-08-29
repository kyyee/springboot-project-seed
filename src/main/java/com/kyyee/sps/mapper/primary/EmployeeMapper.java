/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.mapper.primary;

import com.kyyee.sps.model.primary.Employee;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author kyyee
 */
@org.apache.ibatis.annotations.Mapper
public interface EmployeeMapper extends
    Mapper<Employee>,
    ConditionMapper<Employee>,
    InsertListMapper<Employee>,
    IdsMapper<Employee>,
    IdListMapper<Employee, Long> {
}
