package com.mvc.sell.console.pojo.bean;

import lombok.Data;

import javax.persistence.Id;
import java.math.BigInteger;
import java.util.Date;

/**
 * Config
 *
 * @author qiyichen
 * @create 2018/3/13 11:04
 */
@Data
public class Config {
    @Id
    private BigInteger id;
    private Integer rechargeStatus;
    private Integer withdrawStatus;
    private Float min;
    private Float max;
    private Float poundage;
    private Date createdAt;
    private Date updatedAt;
    private String tokenName;
    private Integer needShow;
    private String contractAddress;
    private Integer decimals;

}
