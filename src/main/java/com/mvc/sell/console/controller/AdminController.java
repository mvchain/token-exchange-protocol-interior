package com.mvc.sell.console.controller;

import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.pojo.dto.AdminDTO;
import com.mvc.sell.console.util.VerifyUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * admin controller
 *
 * @author qiyichen
 * @create 2018/3/10 17:30
 */
@RestController
@RequestMapping("admin")
public class AdminController extends BaseController {

    /**
     * admin login, user check annotation
     *
     * @param adminDTO
     * @return
     */
    @ApiOperation("管理员登录")
    @PostMapping
    Result login(@RequestBody @Valid AdminDTO adminDTO, HttpSession session) throws IllegalAccessException {
        check(session.getId(), "image", adminDTO.getImageCode());
        return ResultGenerator.genSuccessResult(adminService.login(adminDTO));
    }

    @PostMapping("token/refresh")
    @ApiOperation("刷新令牌")
    Result refresh() {
        return ResultGenerator.genSuccessResult(adminService.refresh());
    }

    @ApiOperation("获取图片验证码, 注意session, 不同服务session注意分离")
    @GetMapping(value = "/validate/image", produces = "image/png")
    public void codeImage(HttpServletResponse response, HttpSession session) throws Exception {
        Object[] objs = VerifyUtil.createImage();
        //将图片输出给浏览器
        BufferedImage image = (BufferedImage) objs[1];
        response.setContentType("image/png");
        OutputStream os = response.getOutputStream();
        ImageIO.write(image, "png", os);
        String key = "imageCheck" + session.getId();
        redisTemplate.opsForValue().set(key, objs[0]);
        redisTemplate.expire(key, 2, TimeUnit.MINUTES);
    }

}
