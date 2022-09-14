/*
 * Copyright (c) 2018.  kyyee All rights reserved.
 */

package com.kyyee.sps.dto.request.query;

import com.kyyee.framework.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author kyyee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeQueryParam extends PageQuery {

    @Schema(description = "搜索关键字")
    private String keyword;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "设备id，多个用英文逗号隔开", name = "org_ids", example = "1076231009206272,1076231009206273")
    private List<Long> orgIds;

    @Schema(description = "开始时间，毫秒时间戳", name = "begin_time")
    private LocalDateTime beginTime;

    @Schema(description = "结束时间，毫秒时间戳", name = "end_time")
    private LocalDateTime endTime;
}
