package com.mvc.sell.console.service;

import com.mvc.sell.console.pojo.bean.Config;
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
            return null;
        }
        Config config = new Config();
        config.setId(tokenId);
        config.setRechargeStatus(1);
        return configMapper.selectOne(config);
    }

    public Config getByPorjectId(BigInteger id) {
        Config config = new Config();
        config.setProjectId(id);
        return configMapper.selectOne(config);
    }

    public void deleteByProjectId(BigInteger id) {
        Config config = new Config();
        config.setProjectId(id);
        configMapper.delete(config);
    }

    public Config getByTokenId(BigInteger tokenId) {
        Config config = new Config();
        config.setId(tokenId);
        return configMapper.selectByPrimaryKey(config);
    }
}
