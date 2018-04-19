package com.mvc.sell.console.service;

import com.mvc.sell.console.constants.RedisConstants;
import com.mvc.sell.console.pojo.bean.Config;
import com.mvc.sell.console.util.Convert;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ConfigService
 *
 * @author qiyichen
 * @create 2018/3/13 11:02
 */
@Service
public class ConfigService extends BaseService {

    ConcurrentHashMap<BigInteger, String> tokenMap = new ConcurrentHashMap<>();

    public List<Config> list() {
        return configMapper.selectAll();
    }

    public Map<String, Config> map() {
        Map<String, Config> map = new HashMap<>();
        for (Config config : configMapper.selectAll()) {
            map.put(config.getTokenName(), config);
        }
        return map;
    }

    public void insert(Config config) {
        String tokenName = config.getTokenName();
        Config configTemp = getConfigByTokenName(tokenName);
        if (null != configTemp) {
            config.setId(configTemp.getId());
            return;
        }
        configMapper.insertSelective(config);
        setUnit(config.getId(), config.getDecimals());
    }

    public Config getConfigByTokenName(String tokenName) {
        Config config = new Config();
        config.setTokenName(tokenName);
        config = configMapper.selectOne(config);
        return config;
    }

    public void update(Config config) {
        insert(config);
        configMapper.updateByPrimaryKeySelective(config);
        setUnit(config.getId(), config.getDecimals());
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

    public Config getByTokenId(BigInteger tokenId) {
        Config config = new Config();
        config.setId(tokenId);
        return configMapper.selectByPrimaryKey(config);
    }

    private void setUnit(BigInteger id, Integer decimals) {
        Arrays.stream(Convert.Unit.values()).forEach(obj -> {
                    int value = obj.getWeiFactor().toString().length() - 1;
                    if (decimals == value) {
                        redisTemplate.opsForValue().set(RedisConstants.UNIT + "#" + id, obj);
                    }
                }
        );
    }

    public BigInteger getIdByContractAddress(String contractAddress) {
        Config config = new Config();
        config.setContractAddress(contractAddress);
        return configMapper.selectOne(config).getId();
    }

    public void initUnit() {
        configMapper.selectAll().stream().forEach(config -> setUnit(config.getId(), config.getDecimals()));
    }

    public String getNameByTokenId(BigInteger tokenId) {
        if (this.tokenMap.get(tokenId) == null) {
            Config config = getByTokenId(tokenId);
            this.tokenMap.put(tokenId, config.getTokenName());
        }
        return this.tokenMap.get(tokenId);
    }
}
