package com.mvc.sell.console.service;

import com.mvc.common.context.BaseContextHandler;
import com.mvc.sell.console.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

/**
 * base service
 *
 * @author qiyichen
 * @create 2018/3/12 14:46
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseService {

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(8);

    @Autowired
    AdminMapper adminMapper;
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    ConfigMapper configMapper;
    @Autowired
    ProjectMapper projectMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    TransactionMapper transactionMapper;
    @Autowired
    CapitalMapper capitalMapper;
    @Autowired
    ProjectSoldMapper tokenSoldMapper;
    @Autowired
    RedisTemplate redisTemplate;

    BigInteger getUserId() {
        return (BigInteger) BaseContextHandler.get("userId");
    }

    String getOrderId(String type) {
        Long sid = redisTemplate.opsForValue().increment(type, 1);
        return type + String.format("%09d", sid);
    }
}
