package com.mvc.sell.console.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * AccountVO
 *
 * @author qiyichen
 * @create 2018/3/13 10:52
 */
@Data
public class AccountVO implements Serializable {
    private static final long serialVersionUID = 8363134325804839458L;

    private BigInteger id;
    private String username;
    private Date createdAt;
    private Date updateAt;
    private Integer status;
    private String phone;
    private Integer orderNum;
}
