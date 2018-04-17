package com.mvc.sell.console.dao;

import com.mvc.sell.console.pojo.bean.Orders;
import com.mvc.sell.console.pojo.dto.OrderDTO;
import com.mvc.sell.console.pojo.vo.OrderVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigInteger;
import java.util.List;

/**
 * OrderMapper
 *
 * @author qiyichen
 * @create 2018/3/13 11:57
 */
public interface OrderMapper extends Mapper<Orders> {

    @Select("SELECT project_id FROM orders WHERE user_id = #{userId} GROUP BY project_id")
    List<BigInteger> getUserProject(BigInteger userId);

    @Update("update orders set order_status = #{orderStatus} where project_id = #{projectId} and order_status = 0")
    void updateStatusByProject(@Param("projectId") BigInteger projectId, @Param("orderStatus") Integer orderStatus);

    @Update("update orders set order_status = #{orderStatus} where project_id = #{projectId} and order_status in(0,2)")
    void retireToken(@Param("projectId") BigInteger projectId, @Param("orderStatus") Integer orderStatus);

    @Select({"<script>",
            "select t1.*, t2.title project_name, t2.status, t2.send_token, t2.retire, t3.sold_eth, t2.eth_number project_eth_number from orders t1, project t2, project_sold t3 where t1.project_id = t2.id AND t2.id = t3.id ",
            "<when test=\"status!=null\">",
            "and t2.status = #{status}",
            "</when>",
            "<when test=\"orderStatus!=null\">",
            "and t1.order_status = #{orderStatus}",
            "</when>",
            "<when test=\"projectId!=null\">",
            "and t1.project_id = #{projectId}",
            "</when>",
            "<when test=\"userId!=null\">",
            "and t1.user_id = #{userId}",
            "</when>",
            "<when test=\"id!=null\">",
            "and t1.id = #{id}",
            "</when>",
            "<when test=\"orderId!=null\">",
            "and t1.order_id = #{orderId}",
            "</when>",
            "</script>"})
    List<OrderVO> listByProject(OrderDTO orderDTO);


}
