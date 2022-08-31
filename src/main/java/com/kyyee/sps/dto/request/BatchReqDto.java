package com.kyyee.sps.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BatchReqDto {

    @NotBlank(message = "id不能为空")
    @Schema(name = "主键，多个用英文逗号隔开", example = "1137168911171588,1076231311196160")
    private String ids;

}
