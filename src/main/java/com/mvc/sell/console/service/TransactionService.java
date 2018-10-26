package com.mvc.sell.console.service;

import com.github.pagehelper.PageInfo;
import com.mvc.common.context.BaseContextHandler;
import com.mvc.sell.console.constants.CommonConstants;
import com.mvc.sell.console.constants.MessageConstants;
import com.mvc.sell.console.constants.RedisConstants;
import com.mvc.sell.console.pojo.bean.Account;
import com.mvc.sell.console.pojo.bean.Capital;
import com.mvc.sell.console.pojo.bean.Config;
import com.mvc.sell.console.pojo.bean.Transaction;
import com.mvc.sell.console.pojo.dto.TransactionDTO;
import com.mvc.sell.console.pojo.vo.TransactionVO;
import com.mvc.sell.console.service.ethernum.ContractService;
import com.mvc.sell.console.service.ethernum.Orders;
import com.mvc.sell.console.util.BeanUtil;
import com.mvc.sell.console.util.Convert;
import com.mvc.sell.console.util.Web3jUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import rx.Subscription;
import rx.functions.Action1;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TransactionService
 *
 * @author qiyichen
 * @create 2018/3/13 12:06
 */
@Service
@Log4j
@Transactional(rollbackFor = RuntimeException.class, propagation = Propagation.REQUIRED)
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
    @Value("${wallet.user}")
    String defaultUser;
    @Value("${wallet.eth}")
    BigDecimal ethLimit;
    @Autowired
    ContractService contractService;
    @Autowired
    XlmService xlmService;
//    @Autowired
//    GodService godService;

    public final static BigInteger DEFAULT_GAS_PRICE = Contract.GAS_PRICE.divide(BigInteger.valueOf(5));
    public final static BigInteger DEFAULT_GAS_LIMIT = Contract.GAS_LIMIT.divide(BigInteger.valueOf(10));

    public PageInfo<TransactionVO> transaction(TransactionDTO transactionDTO) {
        transactionDTO.setOrderId(StringUtils.isEmpty(transactionDTO.getOrderId()) ? null : transactionDTO.getOrderId());
        Transaction transaction = (Transaction) BeanUtil.copyProperties(transactionDTO, new Transaction());
        List<Transaction> list = transactionMapper.select(transaction);
        PageInfo pageInfo = new PageInfo(list);
        PageInfo<TransactionVO> result = (PageInfo<TransactionVO>) BeanUtil.beanList2VOList(pageInfo, TransactionVO.class);
        result.getList().forEach(obj -> obj.setTokenName(configService.getNameByTokenId(obj.getTokenId())));
        return result;
    }

    public void approval(BigInteger id, Integer status) throws Exception {
        Assert.isTrue(status != 2, MessageConstants.getMsg("STATUS_ERROR"));
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setStatus(status);
        transactionMapper.updateByPrimaryKeySelective(transaction);
        setBalance(id, status);
        // 发起提币
        sendValue(id, status);
    }

    private void setBalance(BigInteger id, Integer status) {
        if (status.equals(4)) {
            Transaction transaction = new Transaction();
            transaction.setId(id);
            transaction = transactionMapper.selectByPrimaryKey(transaction);
            capitalMapper.updateBalance(transaction.getUserId(), transaction.getTokenId(), transaction.getNumber());
        }
    }

    private void sendValue(BigInteger id, Integer status) throws Exception {
        if (status.equals(1)) {
            Transaction transaction = new Transaction();
            transaction.setId(id);
            transaction = transactionMapper.selectByPrimaryKey(transaction);
            String config = getContractAddressByTokenId(transaction);
            if (org.apache.commons.lang3.StringUtils.isNotBlank(config) && (config.equalsIgnoreCase("XLM") || config.startsWith("XLM-"))) {
                xlmService.sendTransaction(transaction, config);
            } else if (org.apache.commons.lang3.StringUtils.isNotBlank(config) && "god".equalsIgnoreCase(config)) {
//                String hash = godService.sendTransaction(transaction, config);
//                transaction.setStatus(CommonConstants.WITHDRAW);
//                transaction.setHash(hash);
//                transactionMapper.updateByPrimaryKeySelective(transaction);
            } else {
                transaction.setNumber(transaction.getRealNumber());
                redisTemplate.opsForList().leftPush(CommonConstants.TOKEN_SELL_TRANS, transaction);
            }
        }
    }

    private String getContractAddressByTokenId(Transaction transaction) {
        return configService.getByTokenId(transaction.getTokenId()).getContractAddress();
    }

    private String sendTransaction(String fromAddress, String toAddress, String contractAddress, BigInteger realNumber, Boolean listen) throws Exception {
        PersonalUnlockAccount flag = admin.personalUnlockAccount(fromAddress, password).send();
        Assert.isTrue(flag.accountUnlocked(), "unlock error");
        org.web3j.protocol.core.methods.request.Transaction transaction = new org.web3j.protocol.core.methods.request.Transaction(
                fromAddress,
                null,
                DEFAULT_GAS_PRICE,
                DEFAULT_GAS_LIMIT,
                toAddress,
                realNumber,
                null
        );
        EthSendTransaction result = null;
        if (null == contractAddress) {
            // send eth
            result = web3j.ethSendTransaction(transaction).send();
        } else {
            // send token
            result = contractService.eth_sendTransaction(transaction, contractAddress);
        }
        Assert.isTrue(result != null && !result.hasError(), null != result.getError() ? result.getError().getMessage() : "发送失败");
        if (listen) {
            redisTemplate.opsForValue().set(RedisConstants.LISTEN_HASH + "#" + result.getTransactionHash(), 1);
        }
        return result.getTransactionHash();
    }

    @Async
    public void startListen() throws InterruptedException {
        try {
            // listen new transaction
            newListen();
        } catch (Exception e) {
            Thread.sleep(3000);
            startListen();
        }
    }

    private void newListen() throws InterruptedException {
        Subscription subscribe = null;
        try {
            subscribe = web3j.transactionObservable().subscribe(
                    tx -> listenTx(tx, false),
                    getOnError()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            if (null == subscribe || subscribe.isUnsubscribed()) {
                Thread.sleep(3000);
                newListen();
            }
        }
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
            TransactionReceipt result2 = web3j.ethGetTransactionReceipt(tx.getHash()).send().getResult();
            Boolean isFail = !"0x".equalsIgnoreCase(tx.getInput()) & result2.getLogs().size() == 0 && null != result2.getGasUsed() && result2.getGasUsed().compareTo(BigInteger.ZERO) > 0;
            if (isFail) {
                transaction.setStatus(CommonConstants.ERROR);
            } else {
                transaction.setStatus(CommonConstants.STATUS_SUCCESS);
            }
            transactionMapper.updateByPrimaryKeySelective(transaction);
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void addressHandler(org.web3j.protocol.core.methods.response.Transaction tx) {
        String to = Web3jUtil.getTo(tx);
        if (to == null) {
            return;
        }
        String key = RedisConstants.LISTEN_ETH_ADDR + "#" + to.toLowerCase();
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
        // 主账户的转出(发送gas)不记录充值记录
        if (tx.getFrom().equalsIgnoreCase(defaultUser)) {
            return;
        }
        // 充值
        Transaction transaction = new Transaction();
        transaction.setHash(hash);
        transaction.setUserId(userId);
        transaction.setTokenId(tokenId);
        transaction.setOrderId(getOrderId(CommonConstants.ORDER_RECHARGE));
        transaction.setType(CommonConstants.RECHARGE);
        TransactionReceipt result2 = null;
        try {
            result2 = web3j.ethGetTransactionReceipt(tx.getHash()).send().getResult();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Boolean isFail = !"0x".equalsIgnoreCase(tx.getInput()) & result2.getLogs().size() == 0 && null != result2.getGasUsed() && result2.getGasUsed().compareTo(BigInteger.ZERO) > 0;
        transaction.setStatus(isFail ? CommonConstants.ERROR : CommonConstants.STATUS_SUCCESS);
        transaction.setToAddress(to);
        transaction.setFromAddress(tx.getFrom());
        BigInteger value = Web3jUtil.isContractTx(tx) ? new BigInteger(tx.getInput().substring(tx.getInput().length() - 64), 16) : tx.getValue();
        transaction.setNumber(Web3jUtil.getValue(value, transaction.getTokenId(), redisTemplate));
        transaction.setPoundage(0f);
        transaction.setRealNumber(transaction.getNumber());
        transactionMapper.insertSelective(transaction);
        if (!isFail) {
            // update balance
            updateBalance(transaction);
            // transfer balance
            this.transferBalance(transaction, Web3jUtil.getColdUser(redisTemplate));
        }
    }

    private void updateBalance(Transaction transaction) {
        Capital capital = new Capital();
        capital.setUserId(transaction.getUserId());
        capital.setTokenId(transaction.getTokenId());
        Capital capitalTemp = capitalMapper.selectOne(capital);
        if (null == capitalTemp) {
            capital.setBalance(transaction.getNumber());
            capitalMapper.insert(capital);
        } else {
            capitalMapper.updateBalance(transaction.getUserId(), transaction.getTokenId(), transaction.getNumber());
        }
    }

    public void transferBalance(final Transaction transaction, String address) {
        try {
            EthGetBalance result = web3j.ethGetBalance(transaction.getToAddress(), DefaultBlockParameterName.LATEST).send();
            BigInteger needBalance = TransactionService.DEFAULT_GAS_LIMIT.multiply(TransactionService.DEFAULT_GAS_PRICE).multiply(BigInteger.valueOf(10));
            BigInteger sendBalance = transaction.getTokenId().equals(BigInteger.ZERO) ? result.getBalance().subtract(needBalance) : Web3jUtil.getWei(transaction.getNumber(), transaction.getTokenId(), redisTemplate);
            // send gas
            sendGasIfNull(transaction, result, needBalance);
            // add transaction queue
            transaction.setFromAddress(transaction.getToAddress());
            transaction.setToAddress(Web3jUtil.getColdUser(redisTemplate));
            transaction.setNumber(Web3jUtil.getValue(sendBalance, transaction.getTokenId(), redisTemplate));
            transaction.setRealNumber(transaction.getNumber());
            transaction.setOrderId(String.format("TOKEN_SELL_T_%s", transaction.getOrderId()));
            redisTemplate.opsForList().leftPush(CommonConstants.TOKEN_SELL_TRANS, transaction);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    private boolean sendGasIfNull(Transaction transaction, EthGetBalance result, BigInteger needBalance) throws IOException {
        if (!result.hasError() && result.getBalance().compareTo(needBalance) < 0) {
            org.web3j.protocol.core.methods.request.Transaction trans = new org.web3j.protocol.core.methods.request.Transaction(
                    defaultUser,
                    null,
                    DEFAULT_GAS_PRICE,
                    DEFAULT_GAS_LIMIT,
                    transaction.getToAddress(),
                    needBalance,
                    null
            );
            admin.personalUnlockAccount(defaultUser, password);
            web3j.ethSendTransaction(trans).send();
            redisTemplate.opsForList().leftPush(RedisConstants.GAS_QUENE, transaction);
            return true;
        }
        return false;
    }

    private BigInteger getTokenId(org.web3j.protocol.core.methods.response.Transaction tx) {
        if (Web3jUtil.isContractTx(tx)) {
            BigInteger tokenId = configService.getIdByContractAddress(tx.getTo());
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

    private void historyListen() throws InterruptedException {
        BigInteger lastBlockNumber = (BigInteger) redisTemplate.opsForValue().get(RedisConstants.LAST_BOLCK_NUMBER);
        if (null == lastBlockNumber) {
            lastBlockNumber = BigInteger.ZERO;
        }
        Subscription subscription = web3j.replayTransactionsObservable(DefaultBlockParameter.valueOf(lastBlockNumber), DefaultBlockParameterName.LATEST).subscribe(
                tx -> listenTx(tx, true),
                getOnError()
        );
        while (true) {
            if (null == subscription || subscription.isUnsubscribed()) {
                Thread.sleep(3000);
                historyListen();
            }
        }
    }

    @Async
    public Integer newAddress() throws IOException {
        Account account = null;
        Integer num = 0;
        String key = CommonConstants.TOKEN_SELL_USER;
        while (null != (account = accountService.getNonAddress()) && redisTemplate.opsForList().size(key) > 0) {
            String address = (String) redisTemplate.opsForList().rightPop(key);
            account.setAddressEth(address);
            accountService.update(account);
            redisTemplate.opsForValue().set(RedisConstants.LISTEN_ETH_ADDR + "#" + address, account.getId());
            num++;
        }
        return num;
    }

    public void initConfig() {
        configService.initUnit();
    }

    @Async
    public void startHistory() {
        try {
            // listen history transaction
            historyListen();
        } catch (Exception e) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            startHistory();
        }
    }

    public void importAccount(List<Map> list) {
        List accounts = redisTemplate.opsForList().range(CommonConstants.TOKEN_SELL_USER, 0, redisTemplate.opsForList().size(CommonConstants.TOKEN_SELL_USER));
        list.stream().forEach(map -> {
            String address = (String) map.get("address");
            if (null != accounts && accounts.contains(address)) {
                return;
            }
            redisTemplate.opsForList().leftPush(CommonConstants.TOKEN_SELL_USER, address);
        });
        Web3jUtil.initColdUser(redisTemplate);
    }

    public Long getAccountSize() {
        String key = CommonConstants.TOKEN_SELL_USER;
        return redisTemplate.opsForList().size(key);
    }

    public List<com.mvc.sell.console.service.ethernum.Orders> getTransactionJson(String type) {
        final String startWith = getStartWith(type);
        String tempKey = CommonConstants.TOKEN_SELL_TRANS_TEMP;
        String key = CommonConstants.TOKEN_SELL_TRANS;
        List<com.mvc.sell.console.service.ethernum.Orders> result = new ArrayList<>();
        getTransactionsTemp(tempKey, key, result);
        List<Transaction> transactionsTemp;
        Function<Orders, BigInteger> comparator = Orders::getNonce;
        Comparator<Orders> byNonce = Comparator.comparing(comparator);
        transactionsTemp = redisTemplate.opsForList().range(tempKey, 0, redisTemplate.opsForList().size(tempKey));
        redisTemplate.delete(tempKey);
        List<Transaction> tempList = transactionsTemp.stream().filter(obj -> obj.getOrderId().indexOf(startWith) < 0).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(tempList)) {
            redisTemplate.opsForList().leftPushAll(tempKey, tempList);
        }
        result = result.stream().filter(obj -> obj.getOrderId().indexOf(startWith) >= 0).distinct().sorted(byNonce).collect(Collectors.toList());
        return result;
    }

    private void getTransactionsTemp(String tempKey, String key, List<Orders> result) {
        List<Transaction> transactionsTemp = redisTemplate.opsForList().range(tempKey, 0, redisTemplate.opsForList().size(tempKey));
        if (null == transactionsTemp) {
            transactionsTemp = new ArrayList<>();
        }
        for (Transaction transaction : transactionsTemp) {
            Orders orders = getOrders(transaction);
            if (orders.getValue().compareTo(BigDecimal.ZERO) > 0) {
                result.add(orders);
            }
        }
        while (redisTemplate.opsForList().size(key) > 0) {
            Transaction transaction = (Transaction) redisTemplate.opsForList().rightPop(key);
            redisTemplate.opsForList().leftPush(tempKey, transaction);
            Orders orders = getOrders(transaction);
            if (orders.getValue().compareTo(BigDecimal.ZERO) > 0) {
                result.add(orders);
            }
        }
        Collections.reverse(result);
        Map<String, Integer> nonceCount = new HashMap<>(result.size());
        for (int i = 0; i < result.size(); i++) {
            Orders orders = result.get(i);
            Integer nonceBase = nonceCount.get(orders.getFromAddress());
            nonceBase = null == nonceBase ? 0 : nonceBase;
            orders.setNonce(getNonce(orders.getFromAddress()).add(BigInteger.valueOf(nonceBase)));
            nonceCount.put(orders.getFromAddress(), nonceBase + 1);
        }
    }

    private String getStartWith(String type) {
        final String startWith;
        if ("all".equalsIgnoreCase(type)) {
            startWith = "";
        } else if ("transaction".equalsIgnoreCase(type)) {
            startWith = "T_T";
        } else {
            startWith = "T_C";
        }
        return startWith;
    }

    private com.mvc.sell.console.service.ethernum.Orders getOrders(Transaction transaction) {
        com.mvc.sell.console.service.ethernum.Orders orders = new com.mvc.sell.console.service.ethernum.Orders();
        orders.setValue(transaction.getNumber());
        orders.setCreatedAt(transaction.getCreatedAt());
        orders.setUpdatedAt(transaction.getUpdatedAt());
        orders.setFromAddress(transaction.getFromAddress());
        orders.setToAddress(transaction.getToAddress());
        orders.setTokenType(configService.getNameByTokenId(transaction.getTokenId()));
        if (!transaction.getOrderId().startsWith("TOKEN_SELL_T_")) {
            orders.setOrderId(String.format("TOKEN_SELL_T_%s", transaction.getOrderId()));
        } else {
            orders.setOrderId(transaction.getOrderId());
        }
        return orders;
    }

    public void importTransaction(List<Map> list) {
        redisTemplate.opsForList().rightPushAll(CommonConstants.TOKEN_SELL_TRANS_LIST, list);
    }

    BigInteger getNonce(String address) {
        EthGetTransactionCount ethGetTransactionCount = null;
        try {
            ethGetTransactionCount = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).sendAsync().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return ethGetTransactionCount.getTransactionCount();
    }

    public void startTransaction() {
        try {
            Map<String, String> map = (Map<String, String>) redisTemplate.opsForList().rightPop(CommonConstants.TOKEN_SELL_TRANS_LIST);
            if (null != map) {
                String orderId = map.get("orderId");
                String signature = map.get("signature");
                EthSendTransaction result = web3j.ethSendRawTransaction(signature).send();
                String tempOrderId = orderId.replaceAll("TOKEN_SELL_T_", "");
                if (tempOrderId.startsWith("T")) {
                    if (result.hasError()) {
                        log.error(result.getError());
                        transactionMapper.updateStatusByOrderId(tempOrderId, CommonConstants.ERROR);
                    } else {
                        transactionMapper.updateHashByOrderId(tempOrderId, result.getTransactionHash());
                        redisTemplate.opsForValue().set(RedisConstants.LISTEN_HASH + "#" + result.getTransactionHash(), 1);
                    }
                }
            }
            Thread.sleep(1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public void updateStatusByHash(String transactionHash) {
        Transaction transaction = new Transaction();
        transaction.setHash(transactionHash);
        transaction = transactionMapper.selectOne(transaction);
        if (null != transaction) {
            transaction.setStatus(CommonConstants.STATUS_SUCCESS);
            transactionMapper.updateByPrimaryKey(transaction);
            String key = RedisConstants.LISTEN_HASH + "#" + transactionHash;
            redisTemplate.delete(key);
        }
    }

    public void insertOrUpdate(Transaction transaction) {
        if (null == transaction) {
            return;
        }
        Transaction temp = new Transaction();
        temp.setHash(transaction.getHash());
        temp = transactionMapper.selectOne(temp);
        if (null == temp) {
            if (null == transaction.getUserId()) {
                return;
            }
            transactionMapper.insertSelective(transaction);
            updateBalance(transaction);
        } else {
            temp.setStatus(CommonConstants.STATUS_SUCCESS);
            transactionMapper.updateByPrimaryKeySelective(temp);
        }
    }

    @Override
    public BigDecimal getBalance() {
        try {
            String tokenName = (String) BaseContextHandler.get("tokenName");
            if ("ETH".equalsIgnoreCase(tokenName)) {
                BigInteger balance = web3j.ethGetBalance(defaultUser, DefaultBlockParameterName.LATEST).send().getBalance();
                return Convert.fromWei(new BigDecimal(balance), Convert.Unit.ETHER);
            } else {
                Config config = configService.getConfigByTokenName(tokenName);
                BigInteger balance = contractService.balanceOf(config.getContractAddress(), defaultUser);
                return Web3jUtil.getValue(balance, config.getId(), redisTemplate);
            }
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
