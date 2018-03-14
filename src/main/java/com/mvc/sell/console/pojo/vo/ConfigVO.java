package com.mvc.sell.console.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * Config vo
 *
 * @author qiyichen
 * @create 2018/3/14 11:04
 */
@Data
public class ConfigVO implements Serializable {
    private static final long serialVersionUID = -1908092063796472159L;

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
