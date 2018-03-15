package com.mvc.sell.console.dao;

import com.mvc.sell.console.pojo.bean.Orders;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigInteger;
import java.util.List;

/**
 * OrderMapper
 *
 * @author qiyichen
 * @create 2018/3/13 11:57
 */
public interface OrderMapper extends Mapper<Orders>{

    @Select("SELECT id FROM orders WHERE user_id = #{userId} GROUP BY project_id")
    List<BigInteger> getUserProject(BigInteger userId);
}
