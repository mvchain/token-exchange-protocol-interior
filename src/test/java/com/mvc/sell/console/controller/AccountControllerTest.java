package com.mvc.sell.console.controller;

import com.mvc.sell.console.pojo.bean.Account;
import com.mvc.sell.console.pojo.dto.UserFindDTO;
import org.junit.Test;

import java.math.BigInteger;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountControllerTest extends BaseTest {
    @Test
    public void list() throws Exception {
        String path = "/account";
        UserFindDTO userFindDTO = new UserFindDTO();
        mockResult = getResult(path, userFindDTO);
        // check login
        mockResult.andExpect(status().is(403));
        userLogin();
        // check wrong param(SQL Injection Attack)
        userFindDTO.setOrderBy("id desc;delete * from user");
        mockResult = getResult(path, userFindDTO);
        mockResult.andExpect(status().is(500));
        // check true param and result
        userFindDTO.setOrderBy("id desc");
        userFindDTO.setId(BigInteger.valueOf(10001L));
        mockResult = getResult(path, userFindDTO);
        mockResult.andExpect(status().is(200));
        mockResult.andExpect(jsonPath("$.data.pageSize", is(1)));
        mockResult.andExpect(jsonPath("$.data.list[0].id", is(10001)));
    }

    @Test
    public void balance() throws Exception {
        String path = "/account/0/balance";
        mockResult = getResult(path, null);
        // check login
        mockResult.andExpect(status().is(403));
        userLogin();
        // check wrong param(SQL Injection Attack)
        path = "/account/afafaaf/balance";
        mockResult = getResult(path, null);
        mockResult.andExpect(status().is(500));
        // check true param and result
        path = "/account/10001/balance";
        mockResult = getResult(path, null);
        mockResult.andExpect(status().is(200));
        mockResult.andExpect(jsonPath("$.data[0].id", is(1)));
    }

    @Test
    public void get() throws Exception {
        String path = "/account/0";
        mockResult = getResult(path, null);
        // check login
        mockResult.andExpect(status().is(403));
        userLogin();
        // check wrong param(SQL Injection Attack)
        path = "/account/9999";
        mockResult = getResult(path, null);
        mockResult.andExpect(status().is(200));
        mockResult.andExpect(jsonPath("$.data", is(NULL_RESULT)));
        // check true param and result
        path = "/account/10001";
        mockResult = getResult(path, null);
        mockResult.andExpect(status().is(200));
        mockResult.andExpect(jsonPath("$.data.id", is(10001)));
    }

    @Test
    public void get1() throws Exception {
        String path = "/account/username?username=111";
        mockResult = getResult(path, null);
        // check login
        mockResult.andExpect(status().is(403));
        userLogin();
        // check wrong param(SQL Injection Attack)
        mockResult = getResult(path, null);
        mockResult.andExpect(status().is(200));
        mockResult.andExpect(jsonPath("$.data", is(NULL_RESULT)));
        // check true param and result
        path = "/account/username?username=375332835@qq.com";
        mockResult = getResult(path, null);
        mockResult.andExpect(status().is(200));
        mockResult.andExpect(jsonPath("$.data.username", is("375332835@qq.com")));
    }

    @Test
    public void create() throws Exception {
        String path = "/account";
        Account account = new Account();
        String username = UUID.randomUUID() + "@qq.com";
        account.setUsername(username);
        account.setPassword("123456");
        account.setPhone("1888888888888");
        account.setTransactionPassword("123456");
        mockResult = postResult(path, account);
        // check login
        mockResult.andExpect(status().is(403));
        userLogin();
        mockResult = postResult(path, account);
        mockResult.andExpect(status().is(200));
        path = "/account/username?username=" + username;
        mockResult = getResult(path, null);
        mockResult.andExpect(status().is(200));
        mockResult.andExpect(jsonPath("$.data.username", is(username)));
    }

    @Test
    public void update() throws Exception {
        String phone = UUID.randomUUID().toString().substring(0, 16);
        String path = "/account";
        Account account = new Account();
        account.setId(BigInteger.valueOf(10001));
        account.setPhone(phone);
        mockResult = putResult(path, account);
        // check login
        mockResult.andExpect(status().is(403));
        userLogin();
        // user id is get from token-sell service in token, user can not change it by request
        mockResult = putResult(path, account);
        mockResult.andExpect(status().is(200));
        path = "/account/10001";
        mockResult = getResult(path, null);
        mockResult.andExpect(status().is(200));
        mockResult.andExpect(jsonPath("$.data.phone", is(phone)));
    }

}