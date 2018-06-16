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
    @Select("SELECT t2.*, t2.id token_id, IFNULL(t1.balance,0) balance FROM capital t1 RIGHT JOIN config t2 ON t1.token_id = t2.id and t1.user_id = #{userId} WHERE t2.need_show = 1")
    List<CapitalVO> selectBalance(Capital capital);

    @Update("update capital set balance = balance + #{balance} where user_id = #{userId} and token_id = #{tokenId}")
    void updateBalance(@Param("userId") BigInteger userId, @Param("tokenId") BigInteger tokenId, @Param("balance") BigDecimal balance);

    @Update("update capital set balance = balance - #{ethNumber} where user_id = #{userId} and token_id = #{tokenId} and balance >= #{ethNumber}")
    Integer updateEth(@Param("userId") BigInteger userId, @Param("ethNumber") BigDecimal ethNumber, @Param("tokenId") BigInteger tokenId);
}
