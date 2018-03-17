package com.mvc.sell.console.dao;

import com.mvc.sell.console.pojo.bean.Account;
import com.mvc.sell.console.pojo.bean.Admin;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

/**
 * account mapper
 *
 * @author qiyichen
 * @create 2018/3/12 14:47
 */
public interface AccountMapper extends Mapper<Account>{
    @Select("select * from account where address_eth is null limit 1")
    Account getNonAddress();
}
