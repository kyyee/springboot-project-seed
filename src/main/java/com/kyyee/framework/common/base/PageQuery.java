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

    private Integer limit;
    private Integer offset;

    public void dispose() {
        if (this.getPageNum() == null) {
            this.setPageNum(1);
        }
        if (this.getPageSize() == null) {
            this.setPageSize(20);
        }
        if (this.getPageNum() <= 0 || this.getPageSize() <= 0) {
            this.setPageNum(0);
            this.setPageSize(10000);
        }
        if (this.getOffset() == null) {
            this.setOffset(0);
        }
        if (this.getLimit() == null) {
            this.setLimit(5);
        }
        if (StringUtils.isEmpty(this.getSortby())) {
            this.setSortby("create_time");
        }
        if (StringUtils.isEmpty(this.getOrder())) {
            this.setOrder("DESC");
        }
    }

    public String orderBy() {
        String primary = this.getSortby() + " " + this.getOrder();
        if (getOrderInfo() == null || getOrderInfo().length <= 0) {
            return primary;
        } else {
            StringBuilder sb = new StringBuilder();
            for (OrderInfo orderInfo : getOrderInfo()) {
                sb.append(orderInfo.getKey()).append(" ").append(orderInfo.getOrder()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            String secondary = sb.toString();
            return primary + "," + secondary;
        }
    }

}

