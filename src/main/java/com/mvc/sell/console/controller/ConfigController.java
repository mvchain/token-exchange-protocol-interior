package com.mvc.sell.console.controller;

import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.common.annotation.NeedLogin;
import com.mvc.sell.console.pojo.bean.Config;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

/**
 * config controller
 *
 * @author qiyichen
 * @create 2018/3/10 16:44
 */
@RestController
@RequestMapping("config")
public class ConfigController extends BaseController {

    @ApiOperation("查询配置列表,需要有是否显示开关")
    @GetMapping
    @NeedLogin
    Result list() {
        return ResultGenerator.genSuccessResult(configService.list());
    }

    @ApiOperation("新增配置(暂时不用,新建项目会新增配置)")
    @PostMapping
    @NeedLogin
    Result insert(@RequestBody @Valid Config config) {
        configService.insert(config);
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("更新项目")
    @PutMapping
    @NeedLogin
    Result update(@RequestBody @Valid Config config) {
        configService.update(config);
        return ResultGenerator.genSuccessResult();
    }
    @ApiIgnore
    @GetMapping(value = "token")
    @NeedLogin
    Result<List<String>> config() {
        return ResultGenerator.genSuccessResult(configService.token());
    }

    ;

}
