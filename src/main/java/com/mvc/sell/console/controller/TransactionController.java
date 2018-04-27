package com.mvc.sell.console.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.common.annotation.NeedLogin;
import com.mvc.sell.console.pojo.dto.TransactionDTO;
import com.mvc.sell.console.pojo.vo.TransactionVO;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

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
    @NeedLogin
    Result<PageInfo<TransactionVO>> list(@ModelAttribute @Valid TransactionDTO transactionDTO) {
        return ResultGenerator.genSuccessResult(transactionService.transaction(transactionDTO));
    }

    @ApiOperation("更新冲提状态 0待审核, 1等待提币(同意,同意后会直接发送,成功后刷新列表可看到hash), 2成功, 9拒绝")
    @PutMapping("{id}/status/{status}")
    @NeedLogin
    Result approval(@PathVariable BigInteger id, @PathVariable Integer status) throws Exception {
        transactionService.approval(id, status);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("导入账户")
    @PostMapping(value = "import/account")
    @NeedLogin
    public Result<Long> importAccount(@RequestBody MultipartFile file) throws Exception {
        String jsonStr = IOUtils.toString(file.getInputStream());
        List<Map> list = JSON.parseArray(jsonStr, Map.class);
        transactionService.importAccount(list);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("获取剩余账户数量, 过少则需要新增导入")
    @GetMapping(value = "account/size")
    @NeedLogin
    public Result<Long> getAccountSize() throws Exception {
        Long size = transactionService.getAccountSize();
        return ResultGenerator.genSuccessResult(size);
    }

    @ApiOperation("下载待处理数据-所有")
    @GetMapping("all/json")
    @NeedLogin
    void getAllJson(HttpServletResponse response) throws IOException {
        List<com.mvc.sell.console.service.ethernum.Orders> accountList = transactionService.getTransactionJson("all");
        response.setContentType("text/plain");
        response.addHeader("Content-Disposition", "attachment; filename=" + String.format("all_%s.json", System.currentTimeMillis()));
        OutputStream os = response.getOutputStream();
        BufferedOutputStream buff = new BufferedOutputStream(os);
        buff.write(JSON.toJSONString(accountList).getBytes("UTF-8"));
        buff.flush();
        buff.close();
    }

    @ApiOperation("下载待处理数据-交易")
    @GetMapping("transaction/json")
    @NeedLogin
    void getTransactionJson(HttpServletResponse response) throws IOException {
        List<com.mvc.sell.console.service.ethernum.Orders> accountList = transactionService.getTransactionJson("transaction");
        response.setContentType("text/plain");
        response.addHeader("Content-Disposition", "attachment; filename=" + String.format("transaction_%s.json", System.currentTimeMillis()));
        OutputStream os = response.getOutputStream();
        BufferedOutputStream buff = new BufferedOutputStream(os);
        buff.write(JSON.toJSONString(accountList).getBytes("UTF-8"));
        buff.flush();
        buff.close();
    }

    @ApiOperation("下载待处理数据-汇总")
    @GetMapping("collect/json")
    @NeedLogin
    void getCollectionJson(HttpServletResponse response) throws IOException {
        List<com.mvc.sell.console.service.ethernum.Orders> accountList = transactionService.getTransactionJson("collect");
        response.setContentType("text/plain");
        response.addHeader("Content-Disposition", "attachment; filename=" + String.format("collect_%s.json", System.currentTimeMillis()));
        OutputStream os = response.getOutputStream();
        BufferedOutputStream buff = new BufferedOutputStream(os);
        buff.write(JSON.toJSONString(accountList).getBytes("UTF-8"));
        buff.flush();
        buff.close();
    }

    @ApiOperation("导入待处理交易")
    @PostMapping(value = "import/transaction")
    @NeedLogin
    public Result<Long> importTransaction(@RequestBody MultipartFile file) throws Exception {
        String jsonStr = IOUtils.toString(file.getInputStream());
        List<Map> list = JSON.parseArray(jsonStr, Map.class);
        transactionService.importTransaction(list);
        return ResultGenerator.genSuccessResult();
    }

}
