package com.kyyee.framework.common.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@Data
public class PageQuery implements Serializable {
    private static final long serialVersionUID = 6320982368059524189L;

    /**
     * 当前页
     */
    @JsonProperty("page_num")
    private Integer pageNum;
    /**
     * 每页的数量
     */
    @JsonProperty("page_size")
    private Integer pageSize;

    /**
     * 排序字段
     */
    @JsonProperty("sortby")
    private String sortby;

    /**
     * 排序规则
     */
    @JsonProperty("order")
    private String order;

    @JsonProperty("order_info")
    private OrderInfo[] orderInfo;

    public void dispose() {
        if (this.pageNum == null) {
            this.setPageNum(1);
        }
        if (this.pageSize == null) {
            this.setPageSize(20);
        }
        if (this.pageNum <= 0 || this.pageSize <= 0) {
            this.setPageNum(0);
            this.setPageSize(10000);
        }
        if (StringUtils.isEmpty(this.sortby)) {
            this.setSortby("create_time");
        }
        if (StringUtils.isEmpty(this.order)) {
            this.setOrder("DESC");
        }
    }

    public String orderBy() {
        return this.getSortby() + " " + this.getOrder();
    }

}

