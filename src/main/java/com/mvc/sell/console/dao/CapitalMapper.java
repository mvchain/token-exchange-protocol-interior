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
    @Select("select t1.*, t2.* from capital t1, config t2 where t2.id = t1.token_id and t1.user_id = #{userId} and t2.need_show = 1")
    List<CapitalVO> selectBalance(Capital capital);

    @Update("update capital set balance = balance + #{balance} where user_id = #{userId} and token_id = #{tokenId}")
    void updateBalance(@Param("userId") BigInteger userId, @Param("tokenId") BigInteger tokenId, @Param("balance") BigDecimal balance);

    @Update("update capital set balance = balance - #{ethNumber} where user_id = #{userId} and token_id = 0 and balance >= #{ethNumber}")
    Integer updateEth(@Param("userId") BigInteger userId, @Param("ethNumber") BigDecimal ethNumber);
}
