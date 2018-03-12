package com.mvc.sell.console.pojo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String uid;
    private Data createdAt;
    private Date updateAt;

}
