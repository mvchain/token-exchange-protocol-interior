package com.mvc.sell.console.dao;

import com.mvc.sell.console.pojo.bean.Capital;
import com.mvc.sell.console.pojo.vo.CapitalVO;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * CapitalMapper
 *
 * @author qiyichen
 * @create 2018/3/13 17:31
 */
public interface CapitalMapper extends Mapper<Capital>{
    @Select("select t1.*, t2.token_name from capital t1, config t2 where t2.id = t1.token_id and t1.user_id = #{userId} ")
    List<CapitalVO> selectBalance(Capital capital);
}
