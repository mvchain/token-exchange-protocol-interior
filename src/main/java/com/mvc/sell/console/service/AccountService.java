package com.mvc.sell.console.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mvc.sell.console.pojo.bean.Account;
import com.mvc.sell.console.pojo.dto.UserFindDTO;
import com.mvc.sell.console.pojo.vo.AccountVO;
import com.mvc.sell.console.util.BeanUtil;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * AccountService
 *
 * @author qiyichen
 * @create 2018/3/12 20:36
 */
@Service
public class AccountService extends BaseService {


    public PageInfo<AccountVO> list(UserFindDTO userFindDTO) {
        Account account = new Account();
        account.setUsername(userFindDTO.getUsername());
        account.setId(userFindDTO.getId());
        List<Account> list = accountMapper.select(account);
        return (PageInfo<AccountVO>) BeanUtil.beanList2VOList(list, AccountVO.class );
    }

    public AccountVO get(BigInteger id) {
        AccountVO accountVO = new AccountVO();
        Account t = new Account();
        t.setId(BigInteger.ONE);
        Account account = accountMapper.selectByPrimaryKey(t);
        return (AccountVO) BeanUtil.copyProperties(account, new AccountVO());
    }
}
