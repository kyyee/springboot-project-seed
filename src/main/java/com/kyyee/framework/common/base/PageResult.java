package com.kyyee.framework.common.base;

import com.github.pagehelper.Page;
import lombok.Data;

@Data
public class PageResult<T> {

    private Pagination paging;

    private T data;

    public PageResult() {
    }

    public static <T> PageResult<T> of(T list, Page<?> page) {
        return of(list, page.getPageNum(), page.getPageSize(), (int) page.getTotal());
    }

    public static <T> PageResult<T> of(T list, long pageNum, long pageSize, int totalNum) {
        PageResult<T> result = new PageResult<>();
        Pagination paging = new Pagination();
        paging.setPageNum(pageNum);
        paging.setPageSize(pageSize);
        paging.setTotalNum(totalNum);
        result.setPaging(paging);
        result.setData(list);
        return result;
    }
}
