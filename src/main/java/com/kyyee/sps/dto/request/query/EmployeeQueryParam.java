/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.dto.request.query;

import com.kyyee.framework.common.base.PageQuery;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kyyee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeQueryParam extends PageQuery {

    @ApiModelProperty(value = "搜索关键字")
    private String keyword;
}