package com.mvc.sell.console.pojo.dto;

import com.mvc.sell.console.common.Page;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * TransactionDTO
 *
 * @author qiyichen
 * @create 2018/3/13 12:06
 */
@Data
public class TransactionDTO extends Page implements Serializable {
    private static final long serialVersionUID = 7753553107052784799L;

    private BigInteger uid;
    private String orderId;
    private Integer type;
    private Integer status;

}
