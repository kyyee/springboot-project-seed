/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.controller;

import com.kyyee.sps.common.component.annotation.ApiVersion;
import com.kyyee.sps.common.component.validated.group.Insert;
import com.kyyee.sps.common.component.validated.group.Update;
import com.kyyee.sps.dto.request.BatchReqDto;
import com.kyyee.sps.dto.request.EmployeeReqDto;
import com.kyyee.sps.dto.request.query.EmployeeQueryParam;
import com.kyyee.sps.dto.response.BatchResDto;
import com.kyyee.sps.dto.response.EmployeeResDto;
import com.kyyee.sps.service.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/${api-prefix}/employees")
@ApiVersion(1)
@Slf4j
@Tag(name = "雇员管理")
@Validated
public class EmployeeController {

    @Resource
    private
    EmployeeService service;

    @GetMapping("/{id}")
    public EmployeeResDto detail(@PathVariable("id") Long id) {
        return service.detail(id);
    }

    @GetMapping
    public Object list(@Validated EmployeeQueryParam param) {
        return service.list(param);
    }

    @PostMapping
    public EmployeeResDto save(@Validated({Insert.class}) @RequestBody EmployeeReqDto reqDto) {
        return service.save(reqDto);
    }

    @DeleteMapping
    public BatchResDto delete(@Validated @RequestBody BatchReqDto ids) {
        return service.delete(ids);
    }

    @PutMapping("/{id}")
    public EmployeeResDto update(@PathVariable Long id, @Validated({Update.class}) @RequestBody EmployeeReqDto reqDto) {
        return service.update(id, reqDto);
    }
}
