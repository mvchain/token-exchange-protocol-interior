package com.mvc.sell.console.config;

import com.mvc.common.context.BaseContextHandler;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * bean confing
 *
 * @author qiyichen
 * @create 2018/3/12 19:38
 */
@Configuration
public class MyRequestInterceptor {

    @Bean
    public RequestInterceptor headerInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                String token = (String) BaseContextHandler.get("Authorization");
                requestTemplate.header("Authorization", token);
                requestTemplate.header("type", "feign");
            }
        };
    }
}
