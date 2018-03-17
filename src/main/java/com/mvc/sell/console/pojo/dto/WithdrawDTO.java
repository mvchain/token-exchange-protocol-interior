package com.mvc.sell.console.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * WithdrawDTO
 *
 * @author qiyichen
 * @create 2018/3/15 15:20
 */
@Data
public class WithdrawDTO implements Serializable {
    private static final long serialVersionUID = -5364677919189328904L;

    private String address;
    private BigDecimal number;
    private String emailCode;
    private String transactionPassword;
    private String tokenName;
}
