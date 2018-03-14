package com.mvc.sell.console.pojo.bean;

import lombok.Data;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * transaction
 *
 * @author qiyichen
 * @create 2018/3/13 12:03
 */
@Data
public class Transaction {

    @Id
    private BigInteger id;
    private BigInteger uid;
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

}
