package com.mvc.sell.console.pojo.bean;

import com.mvc.sell.console.constants.MessageConstants;
import lombok.Data;
import org.web3j.abi.datatypes.Int;

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
    private Integer rechargeStatus;
    private Integer withdrawStatus;
    private Float min;
    private Float max;
    private Float poundage;
    private Date createdAt;
    private Date updatedAt;
    private BigInteger projectId;
    private String tokenName;
    private Integer needShow;

}
