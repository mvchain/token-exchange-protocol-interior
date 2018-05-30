package com.mvc.sell.console.job;

import com.mvc.sell.console.constants.RedisConstants;
import com.mvc.sell.console.service.TransactionService;
import lombok.extern.java.Log;
import org.glassfish.jersey.media.sse.EventSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.stellar.sdk.Server;
import org.stellar.sdk.requests.EventListener;
import org.stellar.sdk.responses.TransactionResponse;

/**
 * @author qyc
 */
@Component
@Order(value = 3)
@Log
public class XlmRunner implements CommandLineRunner {

    @Autowired
    Server server;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    TransactionService transactionService;

    @Override
    @Async
    public void run(String... args) throws InterruptedException {
        try {
            start();
        } catch (Exception e) {
            start();
        }
    }

    private void start() {
        String token = (String) redisTemplate.opsForValue().get(RedisConstants.LAST_TOKEN_XLM);
        EventListener listert = new EventListener() {
            @Override
            public void onEvent(Object o) {
                TransactionResponse response = null;
                try {
                    response = (TransactionResponse) o;
                    if (null == response || null == response.getHash()) {
                        return;
                    }
                    String key = RedisConstants.LISTEN_HASH + "#" + response.getHash();
                    if (null == redisTemplate.opsForValue().get(key)) {
                        return;
                    }
                    transactionService.updateStatusByHash(response.getHash());
                } catch (Exception e) {
                    // not PaymentOperationResponse
                    e.printStackTrace();
                } finally {
                    redisTemplate.opsForValue().set(RedisConstants.LAST_TOKEN_XLM, response.getPagingToken());
                }
            }
        };
        EventSource st = server.transactions().cursor(token).stream(listert);
        while (true) {
            if (!st.isOpen()) {
                st.open();
            }
        }
    }

};