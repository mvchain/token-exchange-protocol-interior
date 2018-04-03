package com.mvc.sell.console.pojo.bean;

import lombok.Data;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Orders
 *
 * @author qiyichen
 * @create 2018/3/13 11:57
 */
@Data
public class Orders {
    @Id
    private BigInteger id;
    private String orderId;
    private Date createdAt;
    private Date updatedAt;
    private BigInteger userId;
    private BigInteger projectId;
    private BigDecimal ethNumber;
    private BigDecimal tokenNumber;
    private Integer orderStatus;

}
