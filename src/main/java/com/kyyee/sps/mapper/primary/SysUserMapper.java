/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.mapper.primary;

import com.kyyee.sps.model.primary.SysUser;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author kyyee
 */
@org.apache.ibatis.annotations.Mapper
public interface SysUserMapper extends
    Mapper<SysUser>,
    ConditionMapper<SysUser>,
    InsertListMapper<SysUser>,
    IdsMapper<SysUser>,
    IdListMapper<SysUser, Long> {
}
