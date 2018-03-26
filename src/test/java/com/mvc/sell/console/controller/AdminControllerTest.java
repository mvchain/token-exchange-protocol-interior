package com.mvc.sell.console.controller;

import com.mvc.sell.console.pojo.dto.AdminDTO;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminControllerTest extends BaseTest {
    @Test
    public void login() throws Exception {
        String path = "/admin";
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUsername("mvc-admin");
        adminDTO.setPassword("");
        // iamgeCode error
        mockResult = postResult(path, adminDTO);
        mockResult.andExpect(status().is(400));
        // password error
        path = "/admin/validate/image";
        getResult(path, null);
        mockResult.andExpect(status().is(400));
        String sessionId = mockResult.andReturn().getRequest().getSession().getId();
        String imageCode = String.valueOf(redisTemplate.opsForValue().get("imageCheck" + sessionId));
        adminDTO.setImageCode(imageCode);
        path = "/admin";
        mockResult = postResult(path, adminDTO);
        mockResult.andExpect(status().is(400));
        // success
        redisTemplate.delete("imageCheck" + sessionId);
        redisTemplate.opsForValue().set("imageCheck" + (Integer.valueOf(sessionId) + 3), imageCode);
        path = "/admin";
        adminDTO.setPassword("e9c295e337aac88d63b0e351dc4d501f");
        adminDTO.setImageCode(imageCode);
        mockResult = postResult(path, adminDTO);
        mockResult.andExpect(status().is(200));
        mockResult.andExpect(jsonPath("$.data.token", notNullValue()));
        mockResult.andExpect(jsonPath("$.data.refreshToken", notNullValue()));
    }

    @Test
    public void refresh() throws Exception {


    }

    @Test
    public void codeImage() throws Exception {
        String path = "/admin/validate/image";
        mockResult = getResult(path, null);
        String sessionId = mockResult.andReturn().getRequest().getSession().getId();
        mockResult.andExpect(status().is(200));
    }

}