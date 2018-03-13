package com.mvc.sell.console.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * OrderDTO
 *
 * @author qiyichen
 * @create 2018/3/13 11:47
 */
@Data
public class OrderDTO implements Serializable{
    private static final long serialVersionUID = 2072598127090643637L;

    private String searchKey;
}
