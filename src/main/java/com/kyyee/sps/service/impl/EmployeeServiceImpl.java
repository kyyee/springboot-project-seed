/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.service.impl;

import com.kyyee.framework.common.base.AutoPage;
import com.kyyee.framework.common.base.PageQuery;
import com.kyyee.framework.common.enums.DeletedStatus;
import com.kyyee.framework.common.exception.BaseErrorCode;
import com.kyyee.framework.common.exception.BaseException;
import com.kyyee.framework.common.utils.SnowFlake;
import com.kyyee.sps.dto.request.BatchReqDto;
import com.kyyee.sps.dto.request.EmployeeReqDto;
import com.kyyee.sps.dto.response.BatchResDto;
import com.kyyee.sps.dto.response.EmployeeResDto;
import com.kyyee.sps.dto.response.bean.FailDetail;
import com.kyyee.sps.mapper.primary.EmployeeMapper;
import com.kyyee.sps.model.primary.Employee;
import com.kyyee.sps.service.EmployeeService;
import com.kyyee.sps.utils.BeanCopyUtils;
import com.kyyee.sps.utils.DataTransformUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author kyyee
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Resource
    private
    EmployeeMapper employeeMapper;

    @Override
    public EmployeeResDto detail(Long id) {
        return BeanCopyUtils.convert(employeeMapper.selectByPrimaryKey(id), EmployeeResDto.class);
    }

    @Override
    @AutoPage
    public Object list(PageQuery pageQuery) {
        return BeanCopyUtils.convertCollection(employeeMapper.selectAll(), EmployeeResDto.class);
    }

    @Override
    public long count() {
        return employeeMapper.selectCount(null);
    }

    @Override
    public EmployeeResDto save(EmployeeReqDto reqDto) {
        Employee employee = BeanCopyUtils.convert(reqDto, Employee.class);
        employee.setId(SnowFlake.getId());
        employee.setCreateTime(LocalDateTime.now());
        employeeMapper.insert(employee);
        return BeanCopyUtils.convert(employee, EmployeeResDto.class);
    }

    @Override
    public BatchResDto delete(BatchReqDto ids) {
        List<Long> idList = DataTransformUtils.string2List(ids.getIds(), Long.class);

        List<Long> successIds = new ArrayList<>();
        List<FailDetail> failIds = new ArrayList<>();
        for (Long id : idList) {
            String failedMessage = null;
            try {
                Employee dispositionInfo = new Employee();
                dispositionInfo.setId(id);
                dispositionInfo.setDeleted(DeletedStatus.DELETED.value());
                int i = employeeMapper.updateByPrimaryKeySelective(dispositionInfo);
                if (i <= 0) {
                    failedMessage = String.format("雇员%s删除失败", id);
                }
            } catch (Exception e) {
                failedMessage = String.format("雇员%s删除失败，失败原因：%s", id, e.getMessage());
            }
            if (!StringUtils.isEmpty(failedMessage)) {
                failIds.add(new FailDetail(id, failedMessage));
            } else {
                successIds.add(id);
            }
        }
        return new BatchResDto(successIds, failIds);
    }

    @Override
    public EmployeeResDto update(Long id, EmployeeReqDto reqDto) {
        Employee primaryEmployee = Optional.ofNullable(employeeMapper.selectByPrimaryKey(id)).orElseThrow(() -> BaseException.of(BaseErrorCode.UPDATE_FAILED));
        primaryEmployee.setUpdateTime(LocalDateTime.now());
        primaryEmployee.setName(reqDto.getName());
        primaryEmployee.setAge(reqDto.getAge());
        primaryEmployee.setOrgIds(reqDto.getOrgIds());
        Employee employee = BeanCopyUtils.convert(reqDto, Employee.class);
        employeeMapper.updateByPrimaryKey(employee);
        return BeanCopyUtils.convert(employee, EmployeeResDto.class);
    }
}
