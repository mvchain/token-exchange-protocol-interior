package com.mvc.sell.console.pojo.bean;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author qiyichen
 * @create 2018/6/15 17:01
 */
@Data
public class Address {

    private BigInteger userId;
    private String tokenType;
    private String address;

}
