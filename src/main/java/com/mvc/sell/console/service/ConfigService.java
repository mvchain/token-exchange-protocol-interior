package com.mvc.sell.console.service;

import com.mvc.common.msg.Result;
import com.mvc.sell.console.constants.CommonConstants;
import com.mvc.sell.console.pojo.bean.Config;
import com.mvc.sell.console.pojo.vo.ConfigVO;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * ConfigService
 *
 * @author qiyichen
 * @create 2018/3/13 11:02
 */
@Service
public class ConfigService extends BaseService {


    public List<Config> list() {
        return configMapper.selectAll();
    }

    public void insert(Config config) {
        configMapper.insertSelective(config);
    }

    public void update(Config config) {
        configMapper.updateByPrimaryKeySelective(config);
    }

    public List<String> token() {
        return configMapper.token();
    }

    public Config get(BigInteger tokenId) {
        if (null == tokenId) {
            return  null;
        }
        Config config = new Config();
        config.setId(tokenId);
        config.setRechargeStatus(1);
        return configMapper.selectOne(config);
    }
}
