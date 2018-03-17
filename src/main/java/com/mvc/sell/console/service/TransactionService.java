package com.mvc.sell.console.service;

import com.github.pagehelper.PageInfo;
import com.mvc.sell.console.constants.CommonConstants;
import com.mvc.sell.console.constants.RedisConstants;
import com.mvc.sell.console.pojo.bean.Account;
import com.mvc.sell.console.pojo.bean.Config;
import com.mvc.sell.console.pojo.bean.Transaction;
import com.mvc.sell.console.pojo.dto.TransactionDTO;
import com.mvc.sell.console.pojo.vo.TransactionVO;
import com.mvc.sell.console.util.BeanUtil;
import com.mvc.sell.console.util.Web3jUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import rx.functions.Action1;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * TransactionService
 *
 * @author qiyichen
 * @create 2018/3/13 12:06
 */
@Service
@Log4j
public class TransactionService extends BaseService {

    @Autowired
    Web3j web3j;
    @Autowired
    Admin admin;
    @Autowired
    ProjectService projectService;
    @Autowired
    AccountService accountService;
    @Autowired
    ConfigService configService;
    @Value("${wallet.password}")
    String password;

    private BigDecimal TOKEN_TRANS_LIMIT = new BigDecimal(1000);
    private BigDecimal ETH_TRANS_LIMIT = new BigDecimal(10);

    public PageInfo<TransactionVO> transaction(TransactionDTO transactionDTO) {
        Transaction transaction = (Transaction) BeanUtil.copyProperties(transactionDTO, new Transaction());
        List<Transaction> list = transactionMapper.select(transaction);
        return (PageInfo<TransactionVO>) BeanUtil.beanList2VOList(list, TransactionVO.class);
    }

    public void approval(BigInteger id, Integer status) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setStatus(status);
        transactionMapper.updateByPrimaryKeySelective(transaction);
    }

    public void startListen() {
        // listen history transaction
        historyListen();
        // listen new transaction
        newListen();
    }

    private void newListen() {
        web3j.transactionObservable().subscribe(
                tx -> listenTx(tx, false),
                getOnError()
        );
    }

    private Action1<Throwable> getOnError() {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                log.error(throwable);
            }
        };
    }

    private void listenTx(org.web3j.protocol.core.methods.response.Transaction tx, Boolean updateLastBlockNumber) {
        addressHandler(tx);
        hashHandler(tx);
        if (updateLastBlockNumber) {
            redisTemplate.opsForValue().set(RedisConstants.LAST_BOLCK_NUMBER, tx.getBlockNumber());
        }
    }

    private void hashHandler(org.web3j.protocol.core.methods.response.Transaction tx) {
        try {
            String key = RedisConstants.LISTEN_HASH + "#" + tx.getHash();
            String hash = tx.getHash();
            if (!redisTemplate.hasKey(key)) {
                return;
            }
            Transaction transaction = new Transaction();
            transaction.setHash(hash);
            transaction = transactionMapper.selectOne(transaction);
            if (null == transaction) {
                return;
            }
            if (web3j.ethGetTransactionByHash(hash).send().hasError()) {
                transaction.setStatus(CommonConstants.ERROR);
            } else {
                transaction.setStatus(CommonConstants.STATUS_SUCCESS);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void addressHandler(org.web3j.protocol.core.methods.response.Transaction tx) {
        String to = Web3jUtil.getTo(tx);
        String key = RedisConstants.LISTEN_ETH_ADDR + "#" + to;
        String hash = tx.getHash();
        BigInteger userId = (BigInteger) redisTemplate.opsForValue().get(key);
        if (null == userId) {
            return;
        }
        if (existHash(hash)) {
            return;
        }
        BigInteger tokenId = getTokenId(tx);
        Config config = configService.get(tokenId);
        if (null == config) {
            return;
        }
        // 充值
        Transaction transaction = new Transaction();
        transaction.setHash(hash);
        transaction.setUserId(userId);
        transaction.setTokenId(tokenId);
        transaction.setOrderId(getOrderId(CommonConstants.ORDER_RECHARGE));
        transaction.setType(CommonConstants.RECHARGE);
        transaction.setStatus(CommonConstants.STATUS_SUCCESS);
        transaction.setToAddress(to);
        transaction.setFromAddress(tx.getFrom());
        transaction.setNumber(Web3jUtil.getValue(tx.getValue(), transaction.getTokenId(), redisTemplate));
        transaction.setPoundage(0f);
        transaction.setRealNumber(transaction.getNumber());
        transactionMapper.insertSelective(transaction);
        // update balance
        capitalMapper.updateBalance(transaction.getUserId(), transaction.getTokenId(), transaction.getNumber());
    }

    private BigInteger getTokenId(org.web3j.protocol.core.methods.response.Transaction tx) {
        if (Web3jUtil.isContractTx(tx)) {
            BigInteger tokenId = transactionMapper.selectTokenIdByContractAddress(tx.getTo());
            return tokenId;
        } else {
            return BigInteger.ZERO;
        }
    }

    private Boolean existHash(String hash) {
        Transaction transaction = new Transaction();
        transaction.setHash(hash);
        if (null != transactionMapper.selectOne(transaction)) {
            // 充值记录已存在, 充值不存在成功以外的状态
            return true;
        }
        return false;
    }

    private void historyListen() {
        BigInteger lastBlockNumber = (BigInteger) redisTemplate.opsForValue().get(RedisConstants.LAST_BOLCK_NUMBER);
        if (null == lastBlockNumber) {
            lastBlockNumber = BigInteger.ZERO;
        }
        web3j.replayTransactionsObservable(DefaultBlockParameter.valueOf(lastBlockNumber), DefaultBlockParameterName.LATEST).subscribe(
                tx -> listenTx(tx, true),
                getOnError()
        );
    }

    public Integer newAddress() throws IOException {
        Account account = null;
        Integer num = 0;
        while (null != (account = accountService.getNonAddress())) {
            NewAccountIdentifier result = admin.personalNewAccount(password).send();
            account.setAddressEth(result.getAccountId());
            accountService.update(account);
            redisTemplate.opsForValue().set(RedisConstants.LISTEN_ETH_ADDR + "#" + result.getAccountId(), account.getId());
            num++;
        }
        return num;
    }
}
