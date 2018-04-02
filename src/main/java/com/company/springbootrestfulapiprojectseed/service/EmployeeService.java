/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.service;

import com.company.springbootrestfulapiprojectseed.domain.EmployeeDO;

import java.util.List;

/**
 * @author kyyee
 */
public interface EmployeeService {

    EmployeeDO getEmployeeByName(String name);

    List<EmployeeDO> listEmployee();

    long countEmployee();

    EmployeeDO saveEmployee(EmployeeDO employee);

    void removeEmployee(String name);

    EmployeeDO updateEmployee(String name, EmployeeDO employee);
}
