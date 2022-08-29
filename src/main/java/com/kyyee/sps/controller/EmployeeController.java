/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.controller;

import com.kyyee.framework.common.base.PageQuery;
import com.kyyee.sps.annotation.RestApiVersion;
import com.kyyee.sps.dto.request.BatchReqDto;
import com.kyyee.sps.dto.request.EmployeeReqDto;
import com.kyyee.sps.dto.response.EmployeeResDto;
import com.kyyee.sps.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
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

    @GetMapping("/{id}")
    public EmployeeResDto detail(@PathVariable("id") Long id) {
        return service.detail(id);
    }

    @GetMapping
    public Object list(PageQuery pageQuery) {
        return service.list(pageQuery);
    }

    @GetMapping("/count")
    public long count() {
        return service.count();
    }

    @PostMapping
    public EmployeeResDto save(@RequestBody EmployeeReqDto reqDto) {
        log.info("{}", reqDto);
        return service.save(reqDto);
    }

    @DeleteMapping
    public void delete(@Validated @RequestBody BatchReqDto ids) {
        service.delete(ids);
    }

    @PutMapping("/{id}")
    public EmployeeResDto update(@PathVariable Long id, @RequestBody EmployeeReqDto reqDto) {
        log.info("{}", reqDto);
        return service.update(id, reqDto);
    }
}
