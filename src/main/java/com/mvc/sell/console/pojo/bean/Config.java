package com.mvc.sell.console.pojo.bean;

import com.mvc.sell.console.constants.MessageConstants;
import lombok.Data;

import javax.persistence.Id;
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
    @Id
    private BigInteger id;
    private int rechargeStatus;
    private int withdrawStatus;
    private float min;
    private float max;
    private float poundage;
    private Date createdAt;
    private Date updatedAt;
    private BigInteger projectId;
    private String tokenName;

}
