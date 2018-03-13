package com.mvc.sell.console.controller;

import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.common.annotation.Check;
import com.mvc.sell.console.pojo.dto.AdminDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

/**
 * admin controller
 *
 * @author qiyichen
 * @create 2018/3/10 17:30
 */
@Controller
@RequestMapping("admin")
public class AdminController extends BaseController {

    /**
     * admin login, user check annotation
     * @param adminDTO
     * @return
     */
    @PostMapping
    @Check(type = {"image"})
    Result login(@RequestBody @Valid AdminDTO adminDTO) {
        return ResultGenerator.genSuccessResult(adminService.login(adminDTO));
    }

}
