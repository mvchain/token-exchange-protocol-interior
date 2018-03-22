package com.mvc.sell.console.controller;

import com.github.pagehelper.PageInfo;
import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.pojo.dto.TransactionDTO;
import com.mvc.sell.console.pojo.vo.TransactionVO;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation("查询冲提记录")
    @GetMapping
    Result<PageInfo<TransactionVO>> list(@ModelAttribute @Valid TransactionDTO transactionDTO) {
        return ResultGenerator.genSuccessResult(transactionService.transaction(transactionDTO));
    }

    @ApiOperation("更新冲提状态 0待审核, 1等待提币(同意,同意后会直接发送,成功后刷新列表可看到hash), 2成功, 9拒绝")
    @PutMapping("{id}/status/{status}")
    Result approval(@PathVariable BigInteger id, @PathVariable Integer status) throws Exception {
        transactionService.approval(id, status);
        return ResultGenerator.genSuccessResult();
    }

}
