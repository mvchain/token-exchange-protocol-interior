package com.mvc.sell.console.service;

import com.github.pagehelper.PageInfo;
import com.mvc.sell.console.constants.MessageConstants;
import com.mvc.sell.console.pojo.bean.Config;
import com.mvc.sell.console.pojo.bean.Orders;
import com.mvc.sell.console.pojo.dto.OrderDTO;
import com.mvc.sell.console.pojo.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.util.List;

/**
 * OrderService
 *
 * @author qiyichen
 * @create 2018/3/13 11:57
 */
@Service
public class OrderService extends BaseService {

    @Autowired
    ConfigService configService;

    public void update(Orders orders) {
        orderMapper.updateByPrimaryKeySelective(orders);
    }

    public PageInfo<OrderVO> list(OrderDTO orderDTO) {
        List<OrderVO> list = orderMapper.listByProject(orderDTO);
        return new PageInfo(list);
    }

    public void updateStatus(BigInteger orderId, Integer orderStatus) {
        Orders orders = new Orders();
        orders.setId(orderId);
        orders = orderMapper.selectByPrimaryKey(orderId);
        Assert.isTrue(orders.getOrderStatus() == 0, MessageConstants.CANNOT_CANCEL);
        orders.setOrderStatus(orderStatus);
        orderMapper.updateByPrimaryKeySelective(orders);
        tokenSoldMapper.updateEth(orders.getProjectId(), orders.getEthNumber());
        capitalMapper.updateBalance(orders.getUserId(), BigInteger.ZERO, orders.getEthNumber());

    }

    public void updateStatusByProject(BigInteger id, Integer orderStatusRetire) {
        orderMapper.updateStatusByProject(id, orderStatusRetire);
    }
}
