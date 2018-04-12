/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.company.springbootrestfulapiprojectseed.v1.dao;

import com.company.springbootrestfulapiprojectseed.v1.domain.EmployeeDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author kyyee
 */
@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeDO, Long> {
    /**
     * 根据雇员姓名获取某一雇员信息
     *
     * @param name 姓名，不能重复
     * @return EmployeeDO
     */
    EmployeeDO findByName(String name);
}
