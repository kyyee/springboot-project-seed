/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.dto.request;

import com.kyyee.sps.common.component.validated.group.Employee;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class EmployeeReqDto {
    @NotNull(message = "id不能为空", groups = {Employee.Update.class})
    @Schema(description = "id，主键雪花id")
    private Long id;
    @NotNull(message = "名称不能为空", groups = {Employee.Insert.class, Employee.Update.class})
    @Schema(description = "名称", required = true)
    private String name;
    @Schema(description = "年龄")
    private Integer age;
    @Size(message = "组织id不能为空", min = 1, groups = {Employee.Insert.class, Employee.Update.class})
    @Schema(description = "组织id", required = true)
    private List<Long> orgIds;

}
