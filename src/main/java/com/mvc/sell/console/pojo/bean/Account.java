package com.mvc.sell.console.pojo.bean;

import lombok.Data;

import javax.persistence.Id;
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
    @Id
    private BigInteger id;
    private String username;
    private Date createdAt;
    private Date updateAt;
    private Integer status;
    private String password;
    private String transactionPassword;
    private String phone;
    private Integer orderNum;
    private String addressEth;

}
