package com.mvc.sell.console.controller;

import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.common.annotation.NeedLogin;
import com.mvc.sell.console.pojo.bean.Config;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    @NeedLogin
    Result list() {
        return ResultGenerator.genSuccessResult(configService.list());
    }

    @PostMapping
    @NeedLogin
    Result insert(@RequestBody @Valid Config config) {
        configService.insert(config);
        return ResultGenerator.genSuccessResult();
    }

    @PutMapping
    @NeedLogin
    Result update(@RequestBody @Valid Config config) {
        configService.update(config);
        return ResultGenerator.genSuccessResult();
    }


    @GetMapping(value = "token")
    Result<List<String>> config(){
        return ResultGenerator.genSuccessResult(configService.token());
    };

}
