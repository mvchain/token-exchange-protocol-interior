package com.mvc.sell.console.service;

import com.mvc.sell.console.pojo.bean.Order;
import com.mvc.sell.console.pojo.dto.OrderDTO;
import com.mvc.sell.console.pojo.vo.OrderVO;
import com.mvc.sell.console.util.BeanUtil;

import java.util.List;

/**
 * OrderService
 *
 * @author qiyichen
 * @create 2018/3/13 11:57
 */
public class OrderService extends BaseService {
    
    public void update(Order order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    public List<OrderVO> list(OrderDTO orderDTO) {
        List<Order> list = orderMapper.selectByKey(orderDTO);
        return (List<OrderVO>) BeanUtil.beanList2VOList(list, OrderVO.class);
    }
}
