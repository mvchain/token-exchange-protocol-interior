package com.mvc.sell.console.service;

import com.github.pagehelper.Page;
import com.mvc.sell.console.pojo.bean.Transaction;
import com.mvc.sell.console.pojo.dto.TransactionDTO;
import com.mvc.sell.console.pojo.vo.TransactionVO;
import com.mvc.sell.console.util.BeanUtil;
import com.sun.xml.internal.rngom.parse.host.Base;

import java.util.List;

/**
 * TransactionService
 *
 * @author qiyichen
 * @create 2018/3/13 12:06
 */
public class TransactionService extends BaseService {


    public List<TransactionVO> transaction(TransactionDTO transactionDTO) {
        Transaction transaction = (Transaction) BeanUtil.copyProperties(transactionDTO, new Transaction());
        List<Transaction> list = transactionMapper.selectByExample(transaction);
        return (List<TransactionVO>) BeanUtil.beanList2VOList(list, TransactionVO.class);
    }

}
