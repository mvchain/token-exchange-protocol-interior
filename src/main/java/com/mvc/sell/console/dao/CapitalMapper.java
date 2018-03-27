package com.mvc.sell.console.dao;

import com.mvc.sell.console.pojo.bean.Capital;
import com.mvc.sell.console.pojo.vo.CapitalVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * CapitalMapper
 *
 * @author qiyichen
 * @create 2018/3/13 17:31
 */
public interface CapitalMapper extends Mapper<Capital> {
    @Select("SELECT t1.*, t1.id token_id, IFNULL(t2.balance,0) FROM config t1 LEFT JOIN capital t2 ON t1.id = t2.token_id WHERE t1.need_show = 1 AND (t2.user_id = #{userId} OR t2.user_id is NULL)")
    List<CapitalVO> selectBalance(Capital capital);

    @Update("update capital set balance = balance + #{balance} where user_id = #{userId} and token_id = #{tokenId}")
    void updateBalance(@Param("userId") BigInteger userId, @Param("tokenId") BigInteger tokenId, @Param("balance") BigDecimal balance);

    @Update("update capital set balance = balance - #{ethNumber} where user_id = #{userId} and token_id = 0 and balance >= #{ethNumber}")
    Integer updateEth(@Param("userId") BigInteger userId, @Param("ethNumber") BigDecimal ethNumber);
}
