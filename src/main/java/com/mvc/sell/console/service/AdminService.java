package com.mvc.sell.console.service;

import com.mvc.sell.console.constants.CommonConstants;
import com.mvc.sell.console.constants.MessageConstants;
import com.mvc.sell.console.constants.RedisConstants;
import com.mvc.sell.console.pojo.bean.Admin;
import com.mvc.sell.console.pojo.dto.AdminDTO;
import com.mvc.sell.console.pojo.vo.TokenVO;
import com.mvc.sell.console.util.JwtHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
        String encrypt = encoder.encode(adminDTO.getPassword());
        admin.setUsername(username);
        admin.setPassword(encrypt);
        admin = adminMapper.selectOne(admin);
        Assert.notNull(admin, MessageConstants.PWD_ERR);
        Assert.isTrue(!CommonConstants.USER_FREEZE.equals(admin.getStatus()), "用户已冻结!");
        redisTemplate.opsForValue().set(RedisConstants.USER_STATUS, admin.getStatus());
        String token = JwtHelper.createToken(username, admin.getId());
        String refreshToken = JwtHelper.createRefresh(username, admin.getId());
        return new TokenVO(token, refreshToken);
    }

}
