package com.mvc.sell.console.controller;

import com.github.pagehelper.Page;
import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.pojo.bean.Order;
import com.mvc.sell.console.pojo.dto.OrderDTO;
import org.mockito.internal.matchers.Or;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * order controller
 *
 * @author qiyichen
 * @create 2018/3/10 17:25
 */
@Controller
@RequestMapping("order")
public class OrderController  extends  BaseController{

    @GetMapping
    Result list (@ModelAttribute @Valid OrderDTO orderDTO) {
        return ResultGenerator.genSuccessResult( orderService.list(orderDTO));
    }

    @PutMapping
    Result update (@RequestBody @Valid Order order) {
        orderService.update(order);
        return ResultGenerator.genSuccessResult();
    }


}
