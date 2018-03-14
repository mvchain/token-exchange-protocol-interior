package com.mvc.sell.console.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.mvc.sell.console.pojo.bean.Transaction;
import com.mvc.sell.console.pojo.dto.TransactionDTO;
import com.mvc.sell.console.pojo.vo.TransactionVO;
import com.mvc.sell.console.util.BeanUtil;
import com.sun.xml.internal.rngom.parse.host.Base;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * TransactionService
 *
 * @author qiyichen
 * @create 2018/3/13 12:06
 */
@Service
public class TransactionService extends BaseService {


    public PageInfo<TransactionVO> transaction(TransactionDTO transactionDTO) {
        Transaction transaction = (Transaction) BeanUtil.copyProperties(transactionDTO, new Transaction());
        List<Transaction> list = transactionMapper.select(transaction);
        return (PageInfo<TransactionVO>) BeanUtil.beanList2VOList(list, TransactionVO.class);
    }

    public void approval(BigInteger id, Integer status) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setStatus(status);
        transactionMapper.updateByPrimaryKeySelective(transaction);
    }
}
