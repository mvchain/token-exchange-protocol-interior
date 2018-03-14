package com.mvc.sell.console.pojo.bean;

import lombok.Data;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * token sold
 *
 * @author qiyichen
 * @create 2018/3/13 19:14
 */
@Data
public class ProjectSold {
    @Id
    private BigInteger id;
    private Integer buyerNum;
    private BigDecimal soldEth;
    private BigDecimal sendToken;

}
