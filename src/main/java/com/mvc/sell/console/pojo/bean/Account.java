package com.mvc.sell.console.pojo.bean;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

/**
 * Account
 *
 * @author qiyichen
 * @create 2018/3/12 20:38
 */
@Data
public class Account {

    private BigInteger id;
    private String uid;
    private String username;
    private String password;
    private Date createdAt;
    private Date updateAt;
    private Integer status;
    private String transactionPassword;
    private String phone;

}
