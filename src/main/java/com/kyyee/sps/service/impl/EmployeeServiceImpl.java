/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.service.impl;

import com.kyyee.framework.common.base.AutoPage;
import com.kyyee.framework.common.base.PageQuery;
import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import com.kyyee.sps.mapper.primary.EmployeeMapper;
import com.kyyee.sps.model.primary.Employee;
import com.kyyee.sps.service.EmployeeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author kyyee
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Resource
    private
    EmployeeMapper employeeMapper;

    @Override
    public Employee getEmployeeByName(String name) {
        return employeeMapper.findByName(name);
    }

    @Override
    @AutoPage
    public Object listEmployee(PageQuery pageQuery) {
        return employeeMapper.selectList(new Employee());
    }

    @Override
    public long countEmployee() {
        return employeeMapper.selectCount(null);
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        employee.setCreateTime(LocalDateTime.now());
        employeeMapper.insert(employee);
        return employee;
    }

    @Override
    public void removeEmployee(Long id) {
        employeeMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Employee updateEmployee(Long id, Employee employee) {
        Employee primaryEmployee = employeeMapper.selectByPrimaryKey(id).orElseThrow(() -> BaseException.of(BaseErrorCode.UPDATE_FAILED));
        primaryEmployee.setUpdateTime(LocalDateTime.now());
        primaryEmployee.setName(employee.getName());
        primaryEmployee.setAge(employee.getAge());
        primaryEmployee.setDepartment(employee.getDepartment());
        employeeMapper.updateByPrimaryKey(employee);
        return employee;
    }
}
