package com.mvc.sell.console.job;

import com.alibaba.fastjson.JSON;
import com.mvc.sell.console.service.TransactionService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.quorum.Quorum;
import rx.Observable;
import rx.Subscription;

/**
 * @author qyc
 */
@Component
@Order(value = 1)
@Log
public class MyStartupRunner implements CommandLineRunner {

    @Autowired
    TransactionService transactionService;


    @Override
    @Async
    public void run(String... args) throws InterruptedException {
            transactionService.startListen();
    }

}