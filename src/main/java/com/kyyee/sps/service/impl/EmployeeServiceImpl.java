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
import com.kyyee.sps.common.utils.BeanCopyUtils;
import com.kyyee.sps.common.utils.DataTransformUtils;
import com.kyyee.sps.dto.request.BatchReqDto;
import com.kyyee.sps.dto.request.EmployeeReqDto;
import com.kyyee.sps.dto.request.query.EmployeeQueryParam;
import com.kyyee.sps.dto.response.BatchResDto;
import com.kyyee.sps.dto.response.EmployeeResDto;
import com.kyyee.sps.dto.response.bean.FailDetail;
import com.kyyee.sps.mapper.primary.EmployeeMapper;
import com.kyyee.sps.model.primary.Employee;
import com.kyyee.sps.service.EmployeeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kyyee
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;

    @Override
    public EmployeeResDto detail(Long id) {
        return BeanCopyUtils.convert(employeeMapper.selectByPrimaryKey(id).orElseThrow(() -> BaseException.of(BaseErrorCode.UPDATE_FAILED)), EmployeeResDto.class);
    }

    @Override
    @AutoPage
    public Object list(PageQuery pageQuery) {
        EmployeeQueryParam queryParam = (EmployeeQueryParam) pageQuery;
        if (StringUtils.isEmpty(queryParam.getKeyword())) {
            queryParam.setKeyword("");
        } else {
            queryParam.setKeyword("%" + queryParam.getKeyword().replace("%", "¿%").replace("_", "¿_") + "%");
        }
        List<Employee> employees = employeeMapper.selectByParam(queryParam);

        return BeanCopyUtils.convertCollection(employees, EmployeeResDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmployeeResDto save(EmployeeReqDto reqDto) {
        Employee employee = BeanCopyUtils.convert(reqDto, Employee.class);
        employee.setId(SnowFlake.getId());
        employee.setCreateTime(LocalDateTime.now());
        employee.prePersist();

        int i = employeeMapper.insert(employee);
        if (i <= 0) {
            throw BaseException.of(BaseErrorCode.RESULT_EMPTY_ERROR.of(), "雇员{}新增失败", reqDto.getName());
        }
        return BeanCopyUtils.convert(employee, EmployeeResDto.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(rollbackFor = Exception.class)
    public EmployeeResDto update(Long id, EmployeeReqDto reqDto) {
        Employee oldEmployee = employeeMapper.selectByPrimaryKey(id).orElseThrow(() -> BaseException.of(BaseErrorCode.UPDATE_FAILED));
        if (ObjectUtils.isEmpty(oldEmployee) || (!ObjectUtils.isEmpty(oldEmployee) && DeletedStatus.DELETED.value().equals(oldEmployee.getDeleted()))) {
            throw BaseException.of(BaseErrorCode.RESULT_EMPTY_ERROR.of(), "雇员{}不存在", reqDto.getId());
        }
        Employee employee = BeanCopyUtils.convert(reqDto, Employee.class);

        employee.preUpdate(oldEmployee);

        int i = employeeMapper.updateByPrimaryKey(employee);
        if (i <= 0) {
            throw BaseException.of(BaseErrorCode.RESULT_EMPTY_ERROR.of(), "雇员{}更新失败", reqDto.getId());
        }
        return BeanCopyUtils.convert(employee, EmployeeResDto.class);
    }
}
