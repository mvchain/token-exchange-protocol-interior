package com.mvc.sell.console.dao;

import com.mvc.sell.console.pojo.bean.Transaction;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

/**
 * TransactionMapper
 *
 * @author qiyichen
 * @create 2018/3/13 12:07
 */
public interface TransactionMapper extends Mapper<Transaction> {
    @Update("update transaction set status = #{status} where order_id = #{orderId}")
    void updateStatusByOrderId(@Param("orderId") String tempOrderId, @Param("status") Integer status);

    @Update("update transaction set hash = #{hash} where order_id = #{orderId}")
    void updateHashByOrderId(@Param("orderId") String tempOrderId, @Param("hash") String transactionHash);
}
