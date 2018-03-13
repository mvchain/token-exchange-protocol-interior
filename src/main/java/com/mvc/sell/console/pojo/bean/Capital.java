package com.mvc.sell.console.pojo.bean;

import lombok.Data;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * capital
 *
 * @author qiyichen
 * @create 2018/3/13 17:28
 */
@Data
public class Capital {
    @Id
    private BigInteger id;
    private BigInteger userId;
    private BigInteger tokenId;
    private BigDecimal balance;

}
