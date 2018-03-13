package com.mvc.sell.console.pojo.bean;

import com.mvc.sell.console.constants.MessageConstants;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;

/**
 * Config
 *
 * @author qiyichen
 * @create 2018/3/13 11:04
 */
@Data
public class Config {

    private BigInteger id;
    @NotNull(message = MessageConstants.TOKEN_EMPTY)
    private String tokenName;
    private int rechargeStatus;
    private int withdrawStatus;
    private float min;
    private float max;
    private float poundage;
    private Date createdAt;
    private Date updatedAt;

}
