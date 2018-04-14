package com.mvc.sell.console.controller;

import com.github.pagehelper.PageInfo;
import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.common.Page;
import com.mvc.sell.console.common.annotation.NeedLogin;
import com.mvc.sell.console.pojo.dto.BuyDTO;
import com.mvc.sell.console.pojo.dto.MyProjectDTO;
import com.mvc.sell.console.pojo.dto.ProjectDTO;
import com.mvc.sell.console.pojo.dto.WithdrawDTO;
import com.mvc.sell.console.pojo.vo.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Map;

/**
 * project controller
 *
 * @author qiyichen
 * @create 2018/3/10 17:21
 */
@RestController
@RequestMapping("project")
public class ProjectController extends BaseController {

    @ApiOperation("查询项目列表")
    @GetMapping
    @NeedLogin
    Result<PageInfo<ProjectVO>> list(@ModelAttribute Page page) {
        return ResultGenerator.genSuccessResult(projectService.list());
    }

    @ApiOperation("新增项目")
    @PostMapping
    @NeedLogin
    Result add(@RequestBody @Valid ProjectDTO project) {
        project.setId(null);
        projectService.insert(project);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("修改项目")
    @PutMapping
    @NeedLogin
    Result update(@RequestBody @Valid ProjectDTO project) {
        projectService.update(project);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("获取单项项目详情")
    @GetMapping("{id}")
    @NeedLogin
    Result<ProjectVO> get(@PathVariable BigInteger id) {
        return ResultGenerator.genSuccessResult(projectService.get(id));
    }

    @ApiOperation("项目销售总数据")
    @GetMapping("{id}/sold")
    @NeedLogin
    Result<ProjectSoldVO> sold(@PathVariable BigInteger id) {
        return ResultGenerator.genSuccessResult(projectService.getSold(id));
    }

    @ApiOperation("获取oss签名")
    @GetMapping("signature")
    @NeedLogin
    Result<Map> doGetSignature(@RequestParam String dir) throws UnsupportedEncodingException {
        return ResultGenerator.genSuccessResult(ossService.doGetSignature(dir));
    }

    @ApiOperation("修改展示状态")
    @PutMapping("{id}/show/{show}")
    @NeedLogin
    Result show(@PathVariable BigInteger id, @PathVariable Integer show) {
        projectService.updateShow(id, show);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("发币")
    @PutMapping("{id}/sendToken/{sendToken}")
    @NeedLogin
    Result sendToken(@PathVariable BigInteger id, @PathVariable Integer sendToken) {
        projectService.sendToken(id, sendToken);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("清退")
    @PutMapping("{id}/retire/{retire}")
    @NeedLogin
    Result retire(@PathVariable BigInteger id, @PathVariable Integer retire) {
        projectService.retire(id, retire);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("删除")
    @DeleteMapping("{id}")
    @NeedLogin
    Result retire(@PathVariable BigInteger id) {
        projectService.delete(id);
        return ResultGenerator.genSuccessResult();
    }

    @ApiIgnore
    @PutMapping("{id}/status/{status}")
    @NeedLogin
    Result<ProjectVO> get(@PathVariable BigInteger id, @PathVariable Integer status) {
        projectService.updateStatus(id, status);
        return ResultGenerator.genSuccessResult();
    }

    @ApiIgnore
    @GetMapping("account/{id}")
    @NeedLogin
    Result<MyProjectVO> getByUser(@ModelAttribute MyProjectDTO myProjectDTO) {
        return ResultGenerator.genSuccessResult(projectService.getByUser(myProjectDTO));
    }

    @ApiIgnore
    @GetMapping("account")
    @NeedLogin
    Result<PageInfo<MyProjectVO>> getListByUser(@ModelAttribute MyProjectDTO myProjectDTO) {
        return ResultGenerator.genSuccessResult(projectService.getListByUser(myProjectDTO));
    }

    @ApiIgnore
    @GetMapping("info/{id}")
    @NeedLogin
    Result<ProjectInfoVO> info(@PathVariable BigInteger id) {
        return ResultGenerator.genSuccessResult(projectService.info(id));
    }

    @ApiIgnore
    @PostMapping("buy")
    @NeedLogin
    Result buy(@RequestBody @Valid BuyDTO buyDTO) {
        projectService.buy(buyDTO);
        return ResultGenerator.genSuccessResult();
    }

    @ApiIgnore
    @GetMapping("config")
    @NeedLogin
    Result<WithdrawInfoVO> getWithdrawConfig(@RequestParam String tokenName) {
        return ResultGenerator.genSuccessResult(projectService.getWithdrawConfig(tokenName));
    }

    @ApiIgnore
    @PostMapping("withdraw")
    @NeedLogin
    Result withdraw(@RequestBody @Valid WithdrawDTO withdrawDTO) {
        projectService.withdraw(withdrawDTO);
        return ResultGenerator.genSuccessResult();
    }

}
