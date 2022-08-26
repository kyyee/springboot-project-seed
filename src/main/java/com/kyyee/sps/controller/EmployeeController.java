/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.controller;

import com.kyyee.framework.common.base.PageQuery;
import com.kyyee.sps.annotation.RestApiVersion;
import com.kyyee.sps.model.primary.Employee;
import com.kyyee.sps.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author kyyee
 */
@RestController
@RequestMapping("/${api-prefix}/employee")
@RestApiVersion(1)
@CrossOrigin
@Slf4j
public class EmployeeController {

    @Resource
    private
    EmployeeService service;

    @GetMapping("/detail/{name}")
    public Employee getEmployeeByName(@PathVariable String name) {
        return service.getEmployeeByName(name);
    }

    @GetMapping
    public Object listEmployee(PageQuery pageQuery) {
        return service.listEmployee(pageQuery);
    }

    @GetMapping("/count")
    public long countEmployee() {
        return service.countEmployee();
    }

    @PostMapping
    public Employee saveEmployee(@RequestBody Employee employee) {
        log.info("{}", employee);
        return service.saveEmployee(employee);
    }

    @DeleteMapping("/{id}")
    public void removeEmployee(@PathVariable Long id) {
        service.removeEmployee(id);
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        log.info("{}", employee);
        return service.updateEmployee(id, employee);
    }
}
