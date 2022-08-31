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
public class Employee extends BaseEntity {
    private String name;

    private Integer age;

    @ColumnType(column = "org_ids", jdbcType = JdbcType.OTHER, typeHandler = ListLongHandler.class)
    private List<Long> orgIds;

}
