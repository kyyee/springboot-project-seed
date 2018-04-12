/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.v1.web;

import com.company.springbootrestfulapiprojectseed.v1.common.HttpResponseUtil;
import com.company.springbootrestfulapiprojectseed.v1.domain.EmployeeDO;
import com.company.springbootrestfulapiprojectseed.v1.dto.StandardResponseDTO;
import com.company.springbootrestfulapiprojectseed.v1.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author kyyee
 */
@RestController
@RequestMapping("/employee")
@CrossOrigin
public class EmployeeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeController.class);

    @Resource
    private
    EmployeeService service;

    @GetMapping("/detail/{name}")
    public StandardResponseDTO getEmployeeByName(@PathVariable String name) {
        return HttpResponseUtil.success(service.getEmployeeByName(name));
    }

    @GetMapping
    public StandardResponseDTO listEmployee() {
        return HttpResponseUtil.success(service.listEmployee());
    }

    @GetMapping("/count")
    public StandardResponseDTO countEmployee() {
        return HttpResponseUtil.success(service.countEmployee());
    }

    @PostMapping
    public StandardResponseDTO saveEmployee(@RequestBody EmployeeDO employee) {
        LOGGER.info("{}", employee);
        return HttpResponseUtil.success(service.saveEmployee(employee));
    }

    @DeleteMapping("/{name}")
    public StandardResponseDTO removeEmployee(@PathVariable String name) {
        service.removeEmployee(name);
        return HttpResponseUtil.success();
    }

    @PutMapping("/{name}")
    public StandardResponseDTO updateEmployee(@PathVariable String name, @RequestBody EmployeeDO employee) {
        LOGGER.info("{}", employee);
        return HttpResponseUtil.success(service.updateEmployee(name, employee));
    }
}
