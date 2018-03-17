package com.mvc.sell.console.job;

import com.mvc.sell.console.service.TransactionService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import rx.Subscription;

import java.io.IOException;

/**
 * eth job
 *
 * @author qiyichen
 * @create 2018/3/16 14:20
 */
@Component
@Log4j
public class EthJob {

    @Autowired
    TransactionService transactionService;
    @Autowired
    Web3j web3j;

    @Scheduled(cron = "*/10 * * * * ?")
    public void updateAddress() throws IOException {
        Integer num = transactionService.newAddress();
        if (num > 0) {
            log.info(String.format("%s user update eth address", num));
        }
    }


}
