package com.mvc.sell.console.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.common.context.BaseContextHandler;
import com.mvc.sell.console.TokenSellConsoleBootstrap;
import com.mvc.sell.console.util.BeanUtil;
import com.mvc.sell.console.util.JwtHelper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * BaseTest
 *
 * @author qiyichen
 * @create 2018/3/22 11:43
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TokenSellConsoleBootstrap.class)
@WebAppConfiguration
public class BaseTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    WebApplicationContext context;
    private final static String KEY = "Authorization";
    protected final static Map NULL_RESULT = new LinkedHashMap();
    MockMvc mockMvc;
    ObjectMapper mapper = new ObjectMapper();

    ResultActions mockResult;

    @Before
    public void setupMockMvc() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    ResultActions postResult(String uri, Object object) throws Exception {
        Object token = BaseContextHandler.get(KEY);
        ResultActions mockResult = mockMvc.perform(
                post(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .header(KEY, null == token ? "" : String.valueOf(token))
                        .sessionAttr("sessionId", "1")
                        .content(mapper.writeValueAsString(object))
        );
        return mockResult;
    }

    ResultActions putResult(String uri, Object object) throws Exception {
        Object token = BaseContextHandler.get(KEY);
        ResultActions mockResult = mockMvc.perform(
                put(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .header(KEY, null == token ? "" : String.valueOf(token))
                        .sessionAttr("id", "1")
                        .content(mapper.writeValueAsString(object))
        );
        return mockResult;
    }

    ResultActions getResult(String uri, Object object) throws Exception {
        MultiValueMap<String, String> map = BeanUtil.beanToStringMap(object);
        Object token = BaseContextHandler.get(KEY);
        ResultActions mockResult = mockMvc.perform(
                get(uri)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .sessionAttr("id", "1")
                        .params(map)
                        .header(KEY, null == token ? "" : String.valueOf(token)
                        )
        );
        return mockResult;
    }

    protected void userLogin() {
        String token = JwtHelper.createToken("testUser", BigInteger.valueOf(10001));
        BaseContextHandler.set(KEY, token);
    }

}
