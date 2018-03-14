package com.mvc.sell.console.controller;

import com.github.pagehelper.PageInfo;
import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.pojo.dto.TransactionDTO;
import com.mvc.sell.console.pojo.vo.TransactionVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigInteger;

/**
 * transaction controller
 *
 * @author qiyichen
 * @create 2018/3/10 17:12
 */
@RestController
@RequestMapping("transaction")
public class TransactionController extends BaseController {

    @GetMapping
    Result<PageInfo<TransactionVO>> list(@ModelAttribute @Valid TransactionDTO transactionDTO) {
        return ResultGenerator.genSuccessResult(transactionService.transaction(transactionDTO));
    }

    @PutMapping("{id}/status/{status}")
    Result approval (@PathVariable BigInteger id, @PathVariable Integer status) {
        transactionService.approval(id, status);
        return ResultGenerator.genSuccessResult();
    }

//    @PostMapping("withdraw")
//    Result verify(@ModelAttribute @Valid WithdrawDTO withdrawDTO) {
//        return transactionService.verify(withdrawDTO);
//    }

}
