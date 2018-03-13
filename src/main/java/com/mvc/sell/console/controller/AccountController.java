package com.mvc.sell.console.controller;

import com.github.pagehelper.Page;
import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.common.annotation.NeedLogin;
import com.mvc.sell.console.pojo.dto.UserFindDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * account controller
 *
 * @author qiyichen
 * @create 2018/3/10 17:17
 */
@RestController
@RequestMapping("account")
public class AccountController extends BaseController {

    @GetMapping
    @NeedLogin
    Result list(@ModelAttribute @Valid UserFindDTO userFindDTO) {
        return ResultGenerator.genSuccessResult(accountService.list(userFindDTO));
    }

    @GetMapping("{id}")
    @NeedLogin
    Result get(@PathVariable BigInteger id) {
        return ResultGenerator.genSuccessResult(accountService.get(id));
    }

    @GetMapping("{id}/balance")
    @NeedLogin
    Result balance(@PathVariable BigInteger id) {
        return ResultGenerator.genSuccessResult(accountService.balance(id));
    }

}
