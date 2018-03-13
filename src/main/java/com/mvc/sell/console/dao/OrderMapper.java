package com.mvc.sell.console.dao;

import com.mvc.sell.console.pojo.bean.Order;
import com.mvc.sell.console.pojo.dto.OrderDTO;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * OrderMapper
 *
 * @author qiyichen
 * @create 2018/3/13 11:57
 */
public interface OrderMapper extends Mapper<Order>{
    @Select("select * from order where order_id like #{searchKey} or uid like #{searchKey}")
    List<Order> selectByKey(OrderDTO orderDTO);
}
