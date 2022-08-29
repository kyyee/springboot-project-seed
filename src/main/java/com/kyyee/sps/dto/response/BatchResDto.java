package com.kyyee.sps.dto.response;

import com.kyyee.sps.dto.response.bean.FailDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BatchResDto {

    @ApiModelProperty(value = "删除成功ids")
    private List<Long> successIds;

    @ApiModelProperty(value = "删除失败ids")
    private List<FailDetail> failIds;

}
