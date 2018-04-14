package com.mvc.sell.console.service;

import com.mvc.common.context.BaseContextHandler;
import com.mvc.sell.console.constants.CommonConstants;
import com.mvc.sell.console.constants.MessageConstants;
import com.mvc.sell.console.constants.RedisConstants;
import com.mvc.sell.console.pojo.bean.Admin;
import com.mvc.sell.console.pojo.dto.AdminDTO;
import com.mvc.sell.console.pojo.vo.TokenVO;
import com.mvc.sell.console.util.JwtHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigInteger;

/**
 * admin service
 *
 * @author qiyichen
 * @create 2018/3/12 14:45
 */
@Service
public class AdminService extends BaseService {

    public TokenVO login(AdminDTO adminDTO) {
        String username = adminDTO.getUsername();
        Admin admin = new Admin();
        admin.setUsername(username);
        admin = adminMapper.selectOne(admin);
        Assert.notNull(admin, MessageConstants.getMsg("PWD_ERR"));
        boolean result = encoder.matches(adminDTO.getPassword(), admin.getPassword());
        Assert.isTrue(result, MessageConstants.getMsg("PWD_ERR"));
        Assert.isTrue(!MessageConstants.getMsg("USER_FREEZE").equals(admin.getStatus()), "用户已冻结!");
        redisTemplate.opsForValue().set(RedisConstants.USER_STATUS, admin.getStatus());
        String token = JwtHelper.createToken(username, admin.getId());
        String refreshToken = JwtHelper.createRefresh(username, admin.getId());
        return new TokenVO(token, refreshToken);
    }

    public String refresh() {
        BigInteger userId = (BigInteger) BaseContextHandler.get("userId");
        String username = (String) BaseContextHandler.get("username");
        return JwtHelper.createToken(username, userId);
    }

}
