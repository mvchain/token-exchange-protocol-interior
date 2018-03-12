package com.mvc.sell.console.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * admin dto
 *
 * @author qiyichen
 * @create 2018/3/12 14:43
 */
@Data
public class AdminDTO implements Serializable{
    private static final long serialVersionUID = -1840405735682750834L;

    private String password;
    private String username;

}
