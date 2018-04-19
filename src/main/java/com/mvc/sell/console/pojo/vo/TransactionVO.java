package com.mvc.sell.console.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Transaction vo
 *
 * @author qiyichen
 * @create 2018/3/13 12:09
 */
@Data
public class TransactionVO implements Serializable {
    private static final long serialVersionUID = -3765613393191632039L;

    private BigInteger id;
    private BigInteger userId;
    private String orderId;
    private Float poundage;
    private Date startAt;
    private Date finishAt;
    private Date createdAt;
    private Date updatedAt;
    private BigDecimal number;
    private BigInteger realNumber;
    private BigInteger tokenId;
    private String fromAddress;
    private String toAddress;
    private String hash;
    private Integer status;
    private Integer type;
    private String tokenName;

}
