package com.kyyee.sps.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BatchReqDto {

    @NotBlank(message = "id不能为空")
    @Schema(description = "主键，多个用英文逗号隔开", example = "1137168911171588,1076231311196160")
    private String ids;

}
