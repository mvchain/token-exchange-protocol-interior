package com.mvc.sell.console.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * project info vo
 *
 * @author qiyichen
 * @create 2018/3/15 14:04
 */
@Data
public class ProjectInfoVO implements Serializable {
    private static final long serialVersionUID = 6633458831706692961L;

    private BigInteger projectId;
    private String tokenName;
    private BigDecimal ethBalance;
    private Float ratio;

}
