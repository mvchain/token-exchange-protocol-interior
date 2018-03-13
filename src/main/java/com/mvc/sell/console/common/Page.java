package com.mvc.sell.console.common;

import lombok.Data;

/**
 * page
 *
 * @author qiyichen
 * @create 2018/3/13 16:54
 */
@Data
public class Page {
    private int pageNum;
    private int pageSize;
    private String orderBy;
}
