package com.mvc.sell.console.dao;

import com.mvc.sell.console.pojo.bean.Transaction;
import com.mvc.sell.console.pojo.dto.TransactionDTO;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * TransactionMapper
 *
 * @author qiyichen
 * @create 2018/3/13 12:07
 */
public interface TransactionMapper extends Mapper<Transaction>{

}
