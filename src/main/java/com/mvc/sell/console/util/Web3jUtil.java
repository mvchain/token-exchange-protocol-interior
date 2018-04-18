package com.mvc.sell.console.util;

import com.mvc.sell.console.constants.RedisConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.web3j.protocol.core.methods.response.Transaction;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author qiyichen
 * @create 2018/3/17 15:21
 */
public class Web3jUtil {

    private final static String ETH_FLAG = "0x";

    public static String getTo(Transaction tx) {
        try {
            String to = tx.getTo();
            // not transfer
            if (null == to) {
                return to;
            }
            // eth transfer
            if (ETH_FLAG.equalsIgnoreCase(tx.getInput())) {
                return to;
            }
            // token transfer
            if (isContractTx(tx)) {
                return "0x" + tx.getInput().substring(34, 74);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean isContractTx(Transaction tx) {
        return tx.getInput().startsWith("0x64906198") || tx.getInput().startsWith("0xa9059cbb");
    }

    public static BigDecimal getValue(BigInteger value, BigInteger tokenId, RedisTemplate redisTemplate) {
        if (null == tokenId) {
            return null;
        }
        if (tokenId.equals(BigInteger.ZERO)) {
            return Convert.fromWei(new BigDecimal(value), Convert.Unit.ETHER);
        }
        String name = redisTemplate.opsForValue().get(RedisConstants.UNIT + "#" + tokenId).toString();
        Convert.Unit unit = Convert.Unit.valueOf(name);
        return Convert.fromWei(new BigDecimal(value), unit);
    }

    public static BigInteger getWei(BigDecimal realNumber, BigInteger tokenId, RedisTemplate redisTemplate) {
        if (null == tokenId || BigInteger.ZERO.equals(tokenId)) {
            return Convert.toWei(realNumber, Convert.Unit.ETHER).toBigInteger();
        } else {
            String name = redisTemplate.opsForValue().get(RedisConstants.UNIT + "#" + tokenId).toString();
            Convert.Unit unit = Convert.Unit.valueOf(name);
            return Convert.toWei(realNumber, unit).toBigInteger();
        }
    }
}
