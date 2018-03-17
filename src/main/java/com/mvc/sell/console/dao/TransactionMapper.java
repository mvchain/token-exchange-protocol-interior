package com.mvc.sell.console.dao;

import com.mvc.sell.console.pojo.bean.Transaction;
import com.mvc.sell.console.pojo.dto.TransactionDTO;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigInteger;
import java.util.List;

/**
 * TransactionMapper
 *
 * @author qiyichen
 * @create 2018/3/13 12:07
 */
public interface TransactionMapper extends Mapper<Transaction>{

    @Select("select t1.id from config t1, project t2 where t1.project_id = t2.id and t1.recharge_status = 1 and t2.contract_address = #{contractAddress}")
    BigInteger selectTokenIdByContractAddress(String contractAddress);
}
