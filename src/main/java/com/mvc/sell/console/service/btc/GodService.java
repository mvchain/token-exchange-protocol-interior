package com.mvc.sell.console.service.btc;

import com.mvc.sell.console.dao.AddressMapper;
import com.mvc.sell.console.pojo.bean.Address;
import com.mvc.sell.console.pojo.bean.Transaction;
import com.mvc.sell.console.service.BaseService;
import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigInteger;

/**
 * @author qiyichen
 * @create 2018/6/15 17:12
 */
@Service
public class GodService extends BaseService {

    @Autowired
    BtcdClient btcdClient;
    @Value("${wallet.god.password}")
    String password;
    @Autowired
    AddressMapper addressMapper;

    public Address newAddress(String tokenName, BigInteger userId) {
        Address address = new Address();
        try {
            String addr = btcdClient.getNewAddress();
            address.setTokenType(tokenName.toUpperCase());
            address.setUserId(userId);
            address.setAddress(addr);
            addressMapper.insert(address);
        } catch (BitcoindException e) {
            e.printStackTrace();
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
        return address;
    }

    public String sendTransaction(Transaction transaction, String config) {
        Exception e = null;
        try {
            Long lock = btcdClient.getInfo().getUnlockedUntil();
            if (null == lock) {
                btcdClient.walletPassphrase(password, 5);
            }
            btcdClient.walletPassphrase(password, 100);
            String result = btcdClient.sendToAddress(transaction.getToAddress(), transaction.getRealNumber());
            return result;
        } catch (Exception e1) {
            e = e1;
        } finally {
            try {
                btcdClient.walletLock();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        Assert.isNull(e, e.getMessage());
        return null;
    }

}
