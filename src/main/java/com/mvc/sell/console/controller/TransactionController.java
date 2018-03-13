package com.mvc.sell.console.controller;

import com.github.pagehelper.Page;
import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.pojo.dto.TransactionDTO;
import com.mvc.sell.console.pojo.vo.TransactionVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * transaction controller
 *
 * @author qiyichen
 * @create 2018/3/10 17:12
 */
@RestController
@RequestMapping("transaction")
public class TransactionController extends BaseController {

    @GetMapping()
    Result<List<TransactionVO>> withdraw(@ModelAttribute @Valid TransactionDTO transactionDTO) {
        return ResultGenerator.genSuccessResult(transactionService.transaction(transactionDTO));
    }

//    @PostMapping("withdraw")
//    Result verify(@ModelAttribute @Valid WithdrawDTO withdrawDTO) {
//        return transactionService.verify(withdrawDTO);
//    }

}
