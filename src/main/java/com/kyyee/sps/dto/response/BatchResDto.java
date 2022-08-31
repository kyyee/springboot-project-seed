package com.kyyee.sps.dto.response;

import com.kyyee.sps.dto.response.bean.FailDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BatchResDto {

    @Schema(name = "删除成功ids")
    private List<Long> successIds;

    @Schema(name = "删除失败ids")
    private List<FailDetail> failIds;

}
