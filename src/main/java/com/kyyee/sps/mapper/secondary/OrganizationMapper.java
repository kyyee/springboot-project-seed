/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.mapper.secondary;

import com.kyyee.sps.model.secondary.Organization;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author kyyee
 */
@org.apache.ibatis.annotations.Mapper
public interface OrganizationMapper extends
    Mapper<Organization>,
    ConditionMapper<Organization>,
    InsertListMapper<Organization>,
    IdsMapper<Organization>,
    IdListMapper<Organization, Long> {
}
