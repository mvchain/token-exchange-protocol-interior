package com.mvc.sell.console.service;

import com.mvc.sell.console.constants.CommonConstants;
import com.mvc.sell.console.constants.RedisConstants;
import com.mvc.sell.console.dao.TransactionMapper;
import com.mvc.sell.console.pojo.bean.Transaction;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.stellar.sdk.*;
import org.stellar.sdk.responses.SubmitTransactionResponse;

import java.io.IOException;

/**
 * XlmService
 *
 * @author qiyichen
 * @create 2018/5/30 14:57
 */
@Service
@Log4j
public class XlmService {

    @Autowired
    private Server server;
    @Value("${wallet.xlm.pv}")
    private String pv;
    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Async
    public void sendTransaction(Transaction transaction, String contract) throws IOException {
        KeyPair fromPair = KeyPair.fromSecretSeed(pv);
        Account from = new Account(fromPair, server.accounts().account(fromPair).getSequenceNumber());
        KeyPair toPair = KeyPair.fromAccountId(transaction.getToAddress());
        Asset sendAsset;
        if ("XLM".equalsIgnoreCase(contract)) {
            sendAsset = new AssetTypeNative();
        } else {
            KeyPair tokenPair = KeyPair.fromAccountId(contract.replaceAll("XLM-", ""));
            sendAsset = new AssetTypeCreditAlphaNum12("BlockSample", tokenPair);
        }
        org.stellar.sdk.Transaction trans = new org.stellar.sdk.Transaction.Builder(from).addOperation(new PaymentOperation.Builder(toPair, sendAsset, String.valueOf(transaction.getRealNumber())).setSourceAccount(fromPair).build()).build();
        trans.sign(fromPair);
        SubmitTransactionResponse result = server.submitTransaction(trans);
        if (result.isSuccess()) {
            transaction.setStatus(1);
            transaction.setHash(result.getHash());
        } else {
            transaction.setStatus(CommonConstants.ERROR);
        }
        transactionMapper.updateByPrimaryKeySelective(transaction);
        String key = RedisConstants.LISTEN_HASH + "#" + result.getHash();
        redisTemplate.opsForValue().set(key, 1);
    }
}
