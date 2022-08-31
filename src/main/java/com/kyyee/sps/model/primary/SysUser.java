/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.model.primary;

import com.kyyee.sps.mapper.primary.handler.ListLongHandler;
import com.kyyee.sps.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import tk.mybatis.mapper.annotation.ColumnType;

import java.util.List;

/**
 * @author kyyee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUser extends BaseEntity {
    private String name;
    private String code;
    private String password;
    private String token;
    private Short status;
    @ColumnType(column = "role_ids", jdbcType = JdbcType.OTHER, typeHandler = ListLongHandler.class)
    private List<Long> roleIds;

}
