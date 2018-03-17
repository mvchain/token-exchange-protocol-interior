package com.mvc.sell.console.controller;

import com.github.pagehelper.PageInfo;
import com.mvc.common.msg.Result;
import com.mvc.common.msg.ResultGenerator;
import com.mvc.sell.console.pojo.dto.OrderDTO;
import com.mvc.sell.console.pojo.vo.OrderVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigInteger;

/**
 * order controller
 *
 * @author qiyichen
 * @create 2018/3/10 17:25
 */
@RestController
@RequestMapping("order")
public class OrderController extends BaseController {

    @ApiOperation("查询订单")
    @GetMapping
    Result<PageInfo<OrderVO>> list(@ModelAttribute @Valid OrderDTO orderDTO) {
        return ResultGenerator.genSuccessResult(orderService.list(orderDTO));
    }

    @ApiOperation("更新订单状态, 取消 = 9, 默认0, 已发币为2")
    @PutMapping("{id}/orderStatus/{orderStatus}")
    Result updateStatus(@PathVariable BigInteger orderId, @PathVariable Integer orderStatus) {
        orderService.updateStatus(orderId, orderStatus);
        return ResultGenerator.genSuccessResult();
    }

}
