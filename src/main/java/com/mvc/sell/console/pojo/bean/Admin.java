package com.mvc.sell.console.pojo.bean;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

/**
 * admin
 *
 * @author qiyichen
 * @create 2018/3/12 14:50
 */
@Data
public class Admin {

    private BigInteger id;
    private String username;
    private String password;
    private Integer status;
    private String headImage;
    private Date createdAt;
    private Date updateAt;

}
