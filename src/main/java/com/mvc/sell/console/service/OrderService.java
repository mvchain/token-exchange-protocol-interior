package com.mvc.sell.console.service;

import com.github.pagehelper.PageInfo;
import com.mvc.sell.console.constants.MessageConstants;
import com.mvc.sell.console.pojo.bean.Orders;
import com.mvc.sell.console.pojo.bean.Project;
import com.mvc.sell.console.pojo.dto.OrderDTO;
import com.mvc.sell.console.pojo.vo.OrderVO;
import com.mvc.sell.console.util.BeanUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OrderService
 *
 * @author qiyichen
 * @create 2018/3/13 11:57
 */
@Service
public class OrderService extends BaseService {

    public void update(Orders orders) {
        orderMapper.updateByPrimaryKeySelective(orders);
    }

    public PageInfo<OrderVO> list(OrderDTO orderDTO) {
        Orders orders = (Orders) BeanUtil.copyProperties(orderDTO, new Orders());
        List<Orders> list = orderMapper.select(orders);
        List<OrderVO> result = list.stream().map(object -> {
            Project project = new Project();
            project.setId(object.getProjectId());
            Project pj = projectMapper.selectByPrimaryKey(project);
            OrderVO instance = (OrderVO) BeanUtil.copyProperties(object, new OrderVO());
            instance.setProjectName(pj.getTokenName());
            instance.setStatus(pj.getStatus());
            return instance;
        }).collect(Collectors.toList());
        return new PageInfo(result);
    }

    public void updateStatus(BigInteger orderId, Integer orderStatus) {
        Orders orders = new Orders();
        orders.setId(orderId);
        orders = orderMapper.selectByPrimaryKey(orderId);
        Assert.isTrue(orders.getOrderStatus() == 0, MessageConstants.CANNOT_CANCEL);
        orders.setOrderStatus(orderStatus);
        orderMapper.updateByPrimaryKeySelective(orders);
    }
}
