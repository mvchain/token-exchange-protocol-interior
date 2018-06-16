package com.mvc.sell.console.service.btc;

import com.mvc.sell.console.pojo.bean.Address;
import com.mvc.sell.console.pojo.bean.Transaction;
import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.domain.Output;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qiyichen
 * @create 2018/6/15 17:12
 */
@Service
public class GodService {

    @Autowired
    BtcdClient btcdClient;

    public Address newAddress(String tokenName, BigInteger userId) {
        Address address = new Address();
        try {
            String addr = btcdClient.getNewAddress();
            address.setTokenType(tokenName.toUpperCase());
            address.setUserId(userId);
            address.setAddress(addr);
        } catch (BitcoindException e) {
            e.printStackTrace();
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
        return address;
    }

    public String sendTransaction(Transaction transaction, String config) {
        Output output = new Output();
        Map<String, BigDecimal> to = new HashMap<>();
        try {
            btcdClient.lockUnspent(true);
            String result = btcdClient.sendFrom(transaction.getFromAddress(), transaction.getToAddress(), transaction.getRealNumber());
            btcdClient.lockUnspent(false);
            return result;
        } catch (BitcoindException e) {
            e.printStackTrace();
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
        return null;
    }

}
