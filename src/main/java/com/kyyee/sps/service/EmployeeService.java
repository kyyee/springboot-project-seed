/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.service;

import com.kyyee.framework.common.base.AutoPage;
import com.kyyee.framework.common.base.PageQuery;
import com.kyyee.sps.dto.request.BatchReqDto;
import com.kyyee.sps.dto.request.EmployeeReqDto;
import com.kyyee.sps.dto.response.BatchResDto;
import com.kyyee.sps.dto.response.EmployeeResDto;

/**
 * @author kyyee
 */
public interface EmployeeService {

    /**
     * 根据雇员姓名获取某一雇员信息
     *
     * @param name 雇员姓名
     * @return Employee
     */
    EmployeeResDto detail(Long name);

    /**
     * 获取全部雇员信息
     *
     * @return List<Employee>
     */
    @AutoPage
    Object list(PageQuery pageQuery);

    /**
     * 统计雇员人数
     *
     * @return long
     */
    long count();

    /**
     * 新增雇员信息，框架会为雇员增加id，创建时间，修改时间
     *
     * @param reqDto 雇员基本信息
     * @return Employee
     */
    EmployeeResDto save(EmployeeReqDto reqDto);

    /**
     * 删除雇员信息
     *
     * @param ids 雇员姓名
     */
    BatchResDto delete(BatchReqDto ids);

    /**
     * 更新雇员信息
     *
     * @param id     雇员姓名
     * @param reqDto 雇员基本信息
     * @return Employee
     */
    EmployeeResDto update(Long id, EmployeeReqDto reqDto);
}
