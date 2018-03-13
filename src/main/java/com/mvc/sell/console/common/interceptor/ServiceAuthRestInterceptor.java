package com.mvc.sell.console.common.interceptor;

import com.github.pagehelper.PageHelper;
import com.mvc.common.context.BaseContextHandler;
import com.mvc.sell.console.common.annotation.Check;
import com.mvc.sell.console.common.annotation.NeedLogin;
import com.mvc.sell.console.common.exception.CheckeException;
import com.mvc.sell.console.constants.MessageConstants;
import com.mvc.sell.console.util.JwtHelper;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.List;

/**
 * @author qyc
 */
public class ServiceAuthRestInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(ServiceAuthRestInterceptor.class);

    private List<String> allowedClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        String token = request.getHeader("Authorization");
        Claims claim = JwtHelper.parseJWT(token);
        NeedLogin loginAnn = handlerMethod.getMethodAnnotation(NeedLogin.class);
        Check checkAnn = handlerMethod.getMethodAnnotation(Check.class);
        checkAnnotation(claim, loginAnn, checkAnn, request.getRequestURI(), request);
        setUserInfo(claim);
        setPage(request);
        return super.preHandle(request, response, handler);
    }

    private void checkAnnotation(Claims claim, NeedLogin loginAnn, Check checkAnn, String uri, HttpServletRequest request) throws LoginException, CheckeException {
        // check login
//        if (null == claim && null != loginAnn) {
//            throw new LoginException(MessageConstants.TOKEN_WRONG);
//        }
        if (null != claim) {
            Boolean isFeign = "feign".equalsIgnoreCase(request.getHeader("type"));
            JwtHelper.check(claim, uri, isFeign);
        }
        // check condition
        if (null != checkAnn) {
            for (String type : checkAnn.type()) {
                String valiCode = getCode(request, type + "Code");
                String username = claim.get("username", String.class);
                String code = (String) redisTemplate.opsForValue().get(type + "Check" + username);
                if (null == valiCode || !valiCode.equalsIgnoreCase(code)) {
                    throw new CheckeException(type + " is wrong");
                } else {
                    redisTemplate.delete(type + "Check" + username);
                }
            }
        }
    }

    private String getCode(HttpServletRequest request, String key) {
        String code = request.getParameter(key);
        return code;
    }

    public void setUserInfo(Claims userInfo) {
        if (null != userInfo) {
            String username = userInfo.get("username", String.class);
            BigInteger userId = userInfo.get("userId", BigInteger.class);
            BaseContextHandler.set("username", username);
            BaseContextHandler.set("userId", userId);
        }
    }

    public void setPage(HttpServletRequest request) {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            String pageNo = request.getParameter("pageNum");
            String pageSize = request.getParameter("pageSize");
            String orderBy = request.getParameter("orderBy");
            if (StringUtils.isNotBlank(pageNo) && StringUtils.isNotBlank(pageSize)) {
                PageHelper.startPage(Integer.valueOf(pageNo), Integer.valueOf(pageSize));
            }
            if (StringUtils.isNotBlank(orderBy)) {
                PageHelper.orderBy(orderBy);
            }
        }
    }

}
