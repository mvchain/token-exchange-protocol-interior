package com.mvc.sell.console.controller;

import com.mvc.sell.console.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

/**
 * base controller
 *
 * @author qiyichen
 * @create 2018/3/10 16:44
 */
@Component
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
}
