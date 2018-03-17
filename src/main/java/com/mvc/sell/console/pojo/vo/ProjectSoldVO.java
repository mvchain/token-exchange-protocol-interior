package com.mvc.sell.console.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * ProjectSoldVO vo
 *
 * @author qiyichen
 * @create 2018/3/14 14:56
 */
@Data
public class ProjectSoldVO implements Serializable {
    private static final long serialVersionUID = 1185097066226411335L;

    private BigInteger id;
    private Integer buyerNum;
    private BigDecimal soldEth;
    private BigDecimal sendToken;
    private BigDecimal ethNumber;
    private BigDecimal tokenNumber;
    private String tokenName;

}
