package com.mvc.sell.console.controller;

import com.github.pagehelper.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.transform.Result;

/**
 * transaction controller
 *
 * @author qiyichen
 * @create 2018/3/10 17:12
 */
@Controller
@RequestMapping("transaction")
public class TransactionController extends BaseController{

    @GetMapping("withdraw")
    Result withdraw(@ModelAttribute Page page, @ModelAttribute @Valid WithdrawDTO withdrawDTO) {
        return  transactionService.withdraw(page, withdrawDTO);
    }

    @PostMapping("withdraw")
    Result verify(@ModelAttribute @Valid WithdrawDTO withdrawDTO) {
        return transactionService.verify(withdrawDTO);
    }

}
