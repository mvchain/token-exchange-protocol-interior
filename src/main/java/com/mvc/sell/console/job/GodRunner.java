package com.mvc.sell.console.job;

import com.mvc.sell.console.constants.CommonConstants;
import com.mvc.sell.console.constants.RedisConstants;
import com.mvc.sell.console.dao.AddressMapper;
import com.mvc.sell.console.pojo.bean.Address;
import com.mvc.sell.console.pojo.bean.Config;
import com.mvc.sell.console.service.ConfigService;
import com.mvc.sell.console.service.TransactionService;
import com.mvc.sell.console.service.btc.GodService;
import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.domain.Block;
import com.neemre.btcdcli4j.core.domain.Transaction;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.stream.Collectors;

/**
 * @author qyc
 */
//@Component
@Order(value = 4)
@Log
public class GodRunner implements CommandLineRunner {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    TransactionService transactionService;
    @Autowired
    BtcdClient btcdClient;
    @Autowired
    ConfigService configService;
    @Autowired
    AddressMapper addressMapper;
    @Autowired
    GodService godService;

    private static BigInteger tokenId = null;


    @Override
    @Async
    public void run(String... args) throws InterruptedException, BitcoindException, IOException, CommunicationException {
        try {
            start();
        } catch (Exception e) {
            start();
        }
    }

    private void start() {
        try {
            String key = (String) redisTemplate.opsForValue().get(RedisConstants.LAST_TOKEN_GOD);
            if (null == key) {
                redisTemplate.opsForValue().set(RedisConstants.LAST_TOKEN_GOD, btcdClient.listSinceBlock().getLastBlock());
            }
            while (true) {
                BigInteger tokenId = getTokenId();
                if (null == tokenId) {
                    continue;
                }
                Thread.sleep(10);
                key = (String) redisTemplate.opsForValue().get(RedisConstants.LAST_TOKEN_GOD);
                Block block = btcdClient.getBlock(key);
                if (block.getConfirmations() == -1) {
                    redisTemplate.opsForValue().set(RedisConstants.LAST_TOKEN_GOD, block.getPreviousBlockHash());
                    continue;
                }
                for (String txId : block.getTx()) {
                    Transaction tx = null;
                    try {
                        tx = btcdClient.getTransaction(txId);
                        com.mvc.sell.console.pojo.bean.Transaction transaction = getTransaction(tx);
                        transactionService.insertOrUpdate(transaction);
                    } catch (Exception e) {
                        // not mine tx
                        log.info(e.getMessage());
                        continue;
                    }
                }
                if (null != block.getNextBlockHash()) {
                    redisTemplate.opsForValue().set(RedisConstants.LAST_TOKEN_GOD, block.getNextBlockHash());
                }
            }
        } catch (Exception e) {
            log.warning(e.getMessage());
            start();
        }
    }

    private com.mvc.sell.console.pojo.bean.Transaction getTransaction(Transaction tx) throws BitcoindException, CommunicationException {
        com.mvc.sell.console.pojo.bean.Transaction transaction = new com.mvc.sell.console.pojo.bean.Transaction();
        BigDecimal value = tx.getAmount().abs();
        String hex = btcdClient.getRawTransaction(tx.getTxId());
        String address = btcdClient.decodeRawTransaction(hex).getVOut().stream().filter(obj -> obj.getValue().compareTo(value) == 0).collect(Collectors.toList()).get(0).getScriptPubKey().getAddresses().get(0);
        transaction.setStatus(CommonConstants.STATUS_SUCCESS);
        transaction.setHash(tx.getTxId());
        transaction.setRealNumber(value);
        transaction.setNumber(value);
        transaction.setOrderId(configService.getOrderId(CommonConstants.ORDER_RECHARGE));
        transaction.setFromAddress("");
        transaction.setToAddress(address);
        transaction.setTokenId(getTokenId());
        transaction.setType(CommonConstants.RECHARGE);
        transaction.setUserId(getUserId(address));
        transaction.setPoundage(0f);
        return transaction;
    }

    public BigInteger getTokenId() {
        if (null == tokenId) {
            Config config = configService.getConfigByTokenName("god");
            if (null != config) {
                tokenId = config.getId();
            }
        }
        return tokenId;
    }

    private BigInteger getUserId(String address) {
        Address addr = new Address();
        addr.setAddress(address);
        addr.setTokenType("GOD");
        addr = addressMapper.selectOne(addr);
        if (null != addr) {
            return addr.getUserId();
        }
        return null;
    }
};