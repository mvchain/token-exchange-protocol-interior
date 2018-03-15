package com.mvc.sell.console.controller;

import com.github.pagehelper.PageInfo;
import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.pojo.bean.Orders;
import com.mvc.sell.console.pojo.dto.OrderDTO;
import com.mvc.sell.console.pojo.vo.OrderVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * order controller
 *
 * @author qiyichen
 * @create 2018/3/10 17:25
 */
@RestController
@RequestMapping("order")
public class OrderController  extends  BaseController{

    @GetMapping
    Result<PageInfo<OrderVO>> list (@ModelAttribute @Valid OrderDTO orderDTO) {
        return ResultGenerator.genSuccessResult( orderService.list(orderDTO));
    }

    @PutMapping
    Result update (@RequestBody @Valid Orders orders) {
        orderService.update(orders);
        return ResultGenerator.genSuccessResult();
    }


}
