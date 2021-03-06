package com.mvc.sell.console.service;

import com.github.pagehelper.PageInfo;
import com.mvc.sell.console.pojo.bean.Account;
import com.mvc.sell.console.pojo.bean.Capital;
import com.mvc.sell.console.pojo.dto.UserFindDTO;
import com.mvc.sell.console.pojo.vo.AccountVO;
import com.mvc.sell.console.pojo.vo.CapitalVO;
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
        PageInfo pageInfo = new PageInfo(list);
        return (PageInfo<AccountVO>) BeanUtil.beanList2VOList(pageInfo, AccountVO.class);
    }

    public AccountVO get(BigInteger id) {
        Account t = new Account();
        t.setId(id);
        Account account = accountMapper.selectByPrimaryKey(t);
        return (AccountVO) BeanUtil.copyProperties(account, new AccountVO());
    }

    public AccountVO getByUserName(String username) {
        Account t = new Account();
        t.setUsername(username);
        Account account = accountMapper.selectOne(t);
        return (AccountVO) BeanUtil.copyProperties(account, new AccountVO());
    }

    public List<CapitalVO> balance(BigInteger id) {
        Capital capital = new Capital();
        capital.setUserId(id);
        return capitalMapper.selectBalance(capital);
    }

    public void create(Account account) {
        accountMapper.insertSelective(account);
    }

    public void update(Account account) {
        accountMapper.updateByPrimaryKeySelective(account);
    }

    public Account getNonAddress() {
        return accountMapper.getNonAddress();
    }

    public Account getAccount(BigInteger userId) {
        Account t = new Account();
        t.setId(userId);
        return accountMapper.selectByPrimaryKey(t);
    }
}
