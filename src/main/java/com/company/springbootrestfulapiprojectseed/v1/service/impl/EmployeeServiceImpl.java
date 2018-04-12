/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.v1.service.impl;

import com.company.springbootrestfulapiprojectseed.v1.dao.EmployeeRepository;
import com.company.springbootrestfulapiprojectseed.v1.domain.EmployeeDO;
import com.company.springbootrestfulapiprojectseed.v1.service.EmployeeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author kyyee
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Resource
    private
    EmployeeRepository employeeRepository;

    @Override
    public EmployeeDO getEmployeeByName(String name) {
        return employeeRepository.findByName(name);
    }

    @Override
    public List<EmployeeDO> listEmployee() {
        return employeeRepository.findAll();
    }

    @Override
    public long countEmployee() {
        return employeeRepository.count();
    }

    @Override
    public EmployeeDO saveEmployee(EmployeeDO employee) {
        employee.setGmtCreate(System.currentTimeMillis());
        return employeeRepository.save(employee);
    }

    @Override
    public void removeEmployee(String name) {
        EmployeeDO employee = employeeRepository.findByName(name);
        employeeRepository.delete(employee);
    }

    @Override
    public EmployeeDO updateEmployee(String name, EmployeeDO employee) {
        EmployeeDO primaryEmployee = employeeRepository.findByName(name);
        primaryEmployee.setGmtModified(System.currentTimeMillis());
        primaryEmployee.setName(employee.getName());
        primaryEmployee.setAge(employee.getAge());
        primaryEmployee.setDepartment(employee.getDepartment());
        return employeeRepository.save(primaryEmployee);
    }
}
