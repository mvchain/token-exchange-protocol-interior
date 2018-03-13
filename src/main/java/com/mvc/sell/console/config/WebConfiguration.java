package com.mvc.sell.console.config;

import com.mvc.common.handler.GlobalExceptionHandler;
import com.mvc.sell.console.common.interceptor.ServiceAuthRestInterceptor;
import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author qyc
 */
@Configuration("admimWebConfig")
@Primary
public class WebConfiguration extends WebMvcConfigurerAdapter {
    @Bean
    GlobalExceptionHandler getGlobalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    private ArrayList<String> getExcludeCommonPathPatterns() {
        ArrayList<String> list = new ArrayList<>();
        String[] urls = {
                "/v2/api-docs",
                "/swagger-resources/**",
                "/cache/**",
                "/api/log/save"
        };
        Collections.addAll(list, urls);
        return list;
    }

    @Bean
    public ServiceAuthRestInterceptor getServiceAuthRestInterceptor() {
        return new ServiceAuthRestInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration addInterceptor = registry.addInterceptor(getServiceAuthRestInterceptor());
        // 排除配置
        addInterceptor.excludePathPatterns("/error");
        addInterceptor.excludePathPatterns("/login**");
        // 拦截配置
        addInterceptor.addPathPatterns("/**");
    }

    @Bean
    Request.Options feignOptions() {
        return new Request.Options(10 * 1000, 10 * 1000);
    }

}
