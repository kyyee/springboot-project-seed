package com.kyyee.sps.dto.response.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FailDetail {

    @ApiModelProperty(value = "失败id", required = true)
    private Long id;

    @ApiModelProperty(value = "失败原因", required = true)
    private String message;
}
