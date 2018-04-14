package com.mvc.sell.console.controller;

import com.mvc.sell.console.constants.MessageConstants;
import com.mvc.sell.console.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

/**
 * base controller
 *
 * @author qiyichen
 * @create 2018/3/10 16:44
 */
@Controller
public class BaseController {

    @Autowired
    AdminService adminService;
    @Autowired
    OrderService orderService;
    @Autowired
    ProjectService projectService;
    @Autowired
    AccountService accountService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    ConfigService configService;
    @Autowired
    OssService ossService;
    @Autowired
    RedisTemplate redisTemplate;

    void check(String user, String type, String valiCode) throws IllegalAccessException {
        String code = (String) redisTemplate.opsForValue().get(type + "Check" + user);
        if (null == valiCode || !valiCode.equalsIgnoreCase(code)) {
            throw new IllegalAccessException(MessageConstants.getMsg("VALI_CODE_ERR"));
        } else {
            redisTemplate.delete(type + "Check" + user);
        }
    }
}
