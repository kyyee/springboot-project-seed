package com.kyyee.sps.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BatchReqDto {

    @NotBlank(message = "id不能为空")
    @ApiModelProperty(value = "主键，多个用英文逗号隔开", example = "1137168911171588,1076231311196160")
    private String ids;

}
