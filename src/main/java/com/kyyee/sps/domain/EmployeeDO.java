/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author kyyee
 */
@Entity
public class EmployeeDO {
    @Id
    @GeneratedValue
//    @GeneratedValue(generator = "uuidGenerator")
//    @GenericGenerator(name = "uuidGenerator", strategy = "uuid")
    private Long id;

    /**
     * 创建时间（非空）
     */
    private Long gmtCreate;

    /**
     * 修改时间（可空）
     */
    private Long gmtModified;

    private String name;

    private Integer age;

    private String department;

    public EmployeeDO() {
    }

    public EmployeeDO(Long gmtCreate, Long gmtModified, String name, Integer age, String department) {
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        this.name = name;
        this.age = age;
        this.department = department;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Long getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Long gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "EmployeeDO{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", department='" + department + '\'' +
                '}';
    }
}
