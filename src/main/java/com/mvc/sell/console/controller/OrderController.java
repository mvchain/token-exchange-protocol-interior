package com.mvc.sell.console.controller;

import com.github.pagehelper.Page;
import com.mvc.common.msg.Result;
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
    Result list (@ModelAttribute Page page, @ModelAttribute @Valid OrderDTO orderDTO) {
        return  orderService.list(page, orderDTO);
    }

    @PutMapping
    Result udpate (@RequestBody @Valid  OrderDTO orderDTO) {
        return orderService.update(orderDTO);
    }


}
