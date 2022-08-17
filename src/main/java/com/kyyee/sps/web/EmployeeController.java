/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.web;

import com.kyyee.sps.annotation.RestApiVersion;
import com.kyyee.sps.common.HttpResponseUtil;
import com.kyyee.sps.domain.EmployeeDO;
import com.kyyee.sps.dto.StandardResponseDTO;
import com.kyyee.sps.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author kyyee
 */
@RestController
@RequestMapping("/{version}/employee")
@RestApiVersion(1)
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
