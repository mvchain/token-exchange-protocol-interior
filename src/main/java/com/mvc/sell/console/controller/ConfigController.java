package com.mvc.sell.console.controller;

import com.mvc.common.msg.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * config controller
 *
 * @author qiyichen
 * @create 2018/3/10 16:44
 */
@Controller
@RequestMapping("config")
public class ConfigController extends BaseController{

    @GetMapping
    @NeedLogin
    Result list () {
        return configService.list();
    }

    @PostMapping
    @NeedLogin
    Result update(@RequestBody @Valid ConfigDTO configDTO) {
        return configService.update(configDTO);
    }

}
