package com.mvc.sell.console.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * WithdrawInfo vo
 *
 * @author qiyichen
 * @create 2018/3/15 14:51
 */
@Data
public class WithdrawInfoVO implements Serializable {
    private static final long serialVersionUID = -8264574570847638003L;
    private BigDecimal balance;
    private String tokenName;
    private float min;
    private float max;
    private float poundage;
    private BigDecimal todayUse;

}
