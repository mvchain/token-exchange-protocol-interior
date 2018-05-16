package com.mvc.sell.console.service.ethernum;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Orders
 *
 * @author qiyichen
 * @create 2018/4/18 15:05
 */
@Data
public class Orders {
    private BigInteger id;
    private String orderId;
    private String tokenType;
    private BigDecimal value;
    private String fromAddress;
    private String toAddress;
    private Date createdAt;
    private Date updatedAt;
    private BigInteger missionId;
    private String signature;
    private BigInteger nonce;

    @Override
    public boolean equals(Object anObject) {
        String o = orderId.startsWith("TOKEN_SELL_T_C") && "ETH".equalsIgnoreCase(tokenType) ? "TOKEN_SELL_T_C" : orderId;
        if (anObject instanceof Orders) {
            Orders obj = (Orders) anObject;
            String ob = obj.orderId.startsWith("TOKEN_SELL_T_C") && "ETH".equalsIgnoreCase(obj.tokenType) ? "TOKEN_SELL_T_C" : obj.orderId;
            return (o + toAddress).equals(ob + obj.toAddress);
        }
        return super.equals(anObject);
    }

    @Override
    public int hashCode() {
        String o = orderId.startsWith("TOKEN_SELL_T_C") && "ETH".equalsIgnoreCase(tokenType) ? "TOKEN_SELL_T_C" : orderId;
        return (o + toAddress).hashCode();
    }

}
