package com.mvc.sell.console.service;

import com.mvc.sell.console.pojo.bean.Account;
import com.mvc.sell.console.pojo.dto.UserFindDTO;
import com.mvc.sell.console.pojo.vo.AccountVO;
import com.mvc.sell.console.util.BeanUtil;

import java.math.BigInteger;
import java.util.List;

/**
 * AccountService
 *
 * @author qiyichen
 * @create 2018/3/12 20:36
 */
public class AccountService extends BaseService {


    public List<AccountVO> list(UserFindDTO userFindDTO) {
        Account account = new Account();
        account.setUsername(userFindDTO.getUsername());
        account.setUid(userFindDTO.getUid());
        account.setId(userFindDTO.getId());
        List<Account> list = accountMapper.selectByExample(account);
        return (List<AccountVO>) BeanUtil.beanList2VOList(list, AccountVO.class );
    }

    public AccountVO get(BigInteger id) {
        AccountVO accountVO = new AccountVO();
        Account account = accountMapper.selectByPrimaryKey(id);
        return (AccountVO) BeanUtil.copyProperties(account, new AccountVO());
    }
}
