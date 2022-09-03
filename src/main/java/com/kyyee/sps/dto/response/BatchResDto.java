package com.kyyee.sps.dto.response;

import com.kyyee.sps.dto.response.bean.FailDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BatchResDto {

    @Schema(description = "删除成功ids")
    private List<Long> successIds;

    @Schema(description = "删除失败ids")
    private List<FailDetail> failIds;

}
