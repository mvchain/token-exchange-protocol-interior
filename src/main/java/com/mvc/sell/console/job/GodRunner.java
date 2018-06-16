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
import java.math.BigInteger;
import java.util.stream.Collectors;

/**
 * @author qyc
 */
@Component
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

    private void start() throws BitcoindException, IOException, CommunicationException, InterruptedException {
        String key = (String) redisTemplate.opsForValue().get(RedisConstants.LAST_TOKEN_BTC);
        if(null == key){
            key = "36c1b15ac8a0bb17298756baee8655cfc005484fe668345cf893eee86e2942a2";
        }
        while (true) {
            BigInteger tokenId = getTokenId();
            if (null == tokenId) {
                continue;
            }
            Thread.sleep(10);
            key = (String) redisTemplate.opsForValue().get(RedisConstants.LAST_TOKEN_BTC);
            Block block = btcdClient.getBlock(key);
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
                redisTemplate.opsForValue().set(RedisConstants.LAST_TOKEN_BTC, block.getNextBlockHash());
            }
        }
    }

    private com.mvc.sell.console.pojo.bean.Transaction getTransaction(Transaction tx) throws BitcoindException, CommunicationException {
        com.mvc.sell.console.pojo.bean.Transaction transaction = new com.mvc.sell.console.pojo.bean.Transaction();
        String hex = btcdClient.getRawTransaction(tx.getTxId());
        String address = btcdClient.decodeRawTransaction(hex).getVOut().stream().filter(obj -> obj.getValue().compareTo(tx.getAmount()) == 0).collect(Collectors.toList()).get(0).getScriptPubKey().getAddresses().get(0);
        transaction.setStatus(CommonConstants.STATUS_SUCCESS);
        transaction.setHash(tx.getTxId());
        transaction.setRealNumber(tx.getAmount());
        transaction.setNumber(tx.getAmount());
        transaction.setOrderId(configService.getOrderId(CommonConstants.ORDER_RECHARGE));
        transaction.setFromAddress("");
        transaction.setToAddress(address);
        transaction.setTokenId(getTokenId());
        transaction.setType(CommonConstants.RECHARGE);
        transaction.setUserId(getUserId(tx.getTo()));
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