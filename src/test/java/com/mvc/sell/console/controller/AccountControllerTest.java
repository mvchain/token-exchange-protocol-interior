package com.mvc.sell.console.controller;

import com.mvc.sell.console.TokenSellConsoleBootstrap;
import com.mvc.sell.console.pojo.dto.UserFindDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigInteger;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

public class AccountControllerTest extends BaseTest {
    @Test
    public void list() throws Exception {
        UserFindDTO userFindDTO = new UserFindDTO();
        mockResult = getResult("/account", userFindDTO);
        // check login
        mockResult.andExpect(status().is(403));
        userLogin();
        // check wrong param(SQL Injection Attack)
        userFindDTO.setOrderBy("id desc;delete * from user");
        mockResult = getResult("/account", userFindDTO);
        mockResult.andExpect(status().is(500));
        // check true param and result
        userFindDTO.setOrderBy("id desc");
        userFindDTO.setId(BigInteger.ZERO);
        mockResult = getResult("/account", userFindDTO);
        mockResult.andExpect(status().is(200));
        mockResult.andExpect(jsonPath("$", is(0)));
    }

    @Test
    public void balance() throws Exception {
    }

    @Test
    public void get() throws Exception {
    }

    @Test
    public void get1() throws Exception {
    }

    @Test
    public void create() throws Exception {
    }

    @Test
    public void update() throws Exception {
    }

}