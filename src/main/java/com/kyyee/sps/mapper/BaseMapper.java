package com.kyyee.sps.mapper;

import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

public interface BaseMapper<T, PK> extends
    Mapper<T>,
    ConditionMapper<T>,
    InsertListMapper<T>,
    IdsMapper<T>,
    IdListMapper<T, PK> {
}
