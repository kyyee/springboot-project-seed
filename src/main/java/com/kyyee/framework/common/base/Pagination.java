package com.kyyee.framework.common.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class Pagination {
    @JsonProperty("total_num")
    private long totalNum;
    @JsonProperty("page_num")
    private long pageNum;
    @JsonProperty("page_size")
    private long pageSize;
}
