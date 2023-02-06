package com.kyyee.sps.mapper;


import io.mybatis.mapper.Mapper;

import java.io.Serializable;

public interface BaseMapper<T, PK extends Serializable> extends
    Mapper<T, PK> {
}
