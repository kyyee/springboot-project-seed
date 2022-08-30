/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.dto.request;

import com.kyyee.sps.common.component.validated.group.Insert;
import com.kyyee.sps.common.component.validated.group.Update;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author kyyee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeReqDto {
    @NotNull(message = "id不能为空", groups = {Update.class})
    @ApiModelProperty(value = "id，主键雪花id")
    private Long id;
    @NotNull(message = "名称不能为空", groups = {Insert.class, Update.class})
    @ApiModelProperty(value = "名称", required = true)
    private String name;
    @ApiModelProperty(value = "年龄")
    private Integer age;
    @Size(message = "组织id不能为空", min = 1, groups = {Insert.class, Update.class})
    @ApiModelProperty(value = "组织id", required = true)
    private List<Long> orgIds;

}