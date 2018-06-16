package com.mvc.sell.console.job;

import com.mvc.sell.console.constants.RedisConstants;
import com.mvc.sell.console.service.TransactionService;
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
        if (null == key) {
            key = "0000000095f5ee67aaade1dfa150b7ae38b3ff068936c9c95a7bb114b6ce8d4f";
        }
        while (true) {
            Thread.sleep(10);
            Block block = btcdClient.getBlock(key);
            for (String txId : block.getTx()) {
                Transaction tx = btcdClient.getTransaction(txId);
                com.mvc.sell.console.pojo.bean.Transaction transaction = getTransaction(tx);
                transactionService.insertOrUpdate(transaction);
            }
            if (null != block.getNextBlockHash()) {
                redisTemplate.opsForValue().set(key, block.getNextBlockHash());
            }
        }


    }

    private com.mvc.sell.console.pojo.bean.Transaction getTransaction(Transaction tx) {
        return null;
    }

};