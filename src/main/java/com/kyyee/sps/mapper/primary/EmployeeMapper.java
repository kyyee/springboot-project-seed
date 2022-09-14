/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.mapper.primary;

import com.kyyee.sps.dto.request.query.EmployeeQueryParam;
import com.kyyee.sps.mapper.BaseMapper;
import com.kyyee.sps.model.primary.Employee;

import java.util.List;

/**
 * @author kyyee
 */
@org.apache.ibatis.annotations.Mapper
public interface EmployeeMapper extends BaseMapper<Employee, Long> {

    List<Employee> selectByParam(EmployeeQueryParam param);
}
