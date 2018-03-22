package com.mvc.sell.console.pojo.dto;

import com.mvc.sell.console.common.Page;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * OrderDTO
 *
 * @author qiyichen
 * @create 2018/3/13 11:47
 */
@Data
public class OrderDTO extends Page implements Serializable {
    private static final long serialVersionUID = 2072598127090643637L;

    private BigInteger id;
    private BigInteger orderId;
    private BigInteger userId;
    private BigInteger projectId;
    private Integer orderStatus;
    private Integer status;

}
