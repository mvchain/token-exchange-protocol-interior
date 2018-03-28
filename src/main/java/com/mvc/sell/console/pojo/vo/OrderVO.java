package com.mvc.sell.console.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * OrderVO
 *
 * @author qiyichen
 * @create 2018/3/13 12:00
 */
@Data
public class OrderVO implements Serializable {
    private static final long serialVersionUID = 4830776689679004765L;


    private BigInteger id;
    private String orderId;
    private Date createdAt;
    private Date updatedAt;
    private BigInteger userId;
    private BigInteger projectId;
    private BigDecimal ethNumber;
    private BigDecimal tokenNumber;
    private Integer orderStatus;
    private String projectName;
    private Integer status;
    private Integer retire;
    private Integer sendToken;

}
