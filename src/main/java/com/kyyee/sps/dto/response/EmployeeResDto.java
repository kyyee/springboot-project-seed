/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.dto.response;

import com.kyyee.sps.model.secondary.Organization;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author kyyee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResDto {
    @Schema(description = "id，主键雪花id", required = true)
    private Long id;

    @Schema(description = "名称", required = true)
    private String name;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "组织id", required = true)
    private List<Long> orgIds;

    @Schema(description = "组织", required = true)
    private List<Organization> orgs;

}
