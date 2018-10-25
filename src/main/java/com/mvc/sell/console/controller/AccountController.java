package com.mvc.sell.console.controller;

import com.github.pagehelper.PageInfo;
import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.common.annotation.NeedLogin;
import com.mvc.sell.console.pojo.bean.Account;
import com.mvc.sell.console.pojo.dto.UserFindDTO;
import com.mvc.sell.console.pojo.vo.AccountVO;
import com.mvc.sell.console.pojo.vo.CapitalVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;

/**
 * account controller
 *
 * @author qiyichen
 * @create 2018/3/10 17:17
 */
@RestController
@RequestMapping("account")
public class AccountController extends BaseController {

    @ApiOperation("查询用户列表")
    @GetMapping
    @NeedLogin
    Result<PageInfo<AccountVO>> list(@ModelAttribute @Valid UserFindDTO userFindDTO) {
        return ResultGenerator.genSuccessResult(accountService.list(userFindDTO));
    }

    @ApiOperation("查询用户余额信息")
    @GetMapping("{id}/balance")
    @NeedLogin
    Result<List<CapitalVO>> balance(@PathVariable BigInteger id) {
        return ResultGenerator.genSuccessResult(accountService.balance(id));
    }

    @ApiIgnore
    @GetMapping("{id}/{tokenName}")
    @NeedLogin
    Result<AccountVO> get(@PathVariable BigInteger id, @PathVariable String tokenName) {
        return ResultGenerator.genSuccessResult(accountService.get( id, tokenName));
    }

    @ApiIgnore
    @GetMapping("username")
    @NeedLogin
    Result<AccountVO> get(@RequestParam String username) {
        return ResultGenerator.genSuccessResult(accountService.getByUserName(username));
    }

    @ApiIgnore
    @PostMapping
    @NeedLogin
    Result create(@RequestBody Account account) {
        accountService.create(account);
        return ResultGenerator.genSuccessResult();
    }

    @ApiIgnore
    @PutMapping
    @NeedLogin
    Result update(@RequestBody Account account) {
        accountService.update(account);
        return ResultGenerator.genSuccessResult();
    }

}
