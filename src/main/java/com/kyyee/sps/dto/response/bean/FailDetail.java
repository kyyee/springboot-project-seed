package com.kyyee.sps.dto.response.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FailDetail {

    @Schema(description = "失败id", required = true)
    private Long id;

    @Schema(description = "失败原因", required = true)
    private String message;
}
