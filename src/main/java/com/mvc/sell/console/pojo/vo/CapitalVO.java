package com.mvc.sell.console.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Capital vo
 *
 * @author qiyichen
 * @create 2018/3/13 17:36
 */
@Data
public class CapitalVO implements Serializable{
    private BigInteger id;
    private BigInteger tokenId;
    private BigDecimal balance;
    private String tokenName;

}
