package com.mvc.sell.console;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-05-25 12:44
 */
@EnableEurekaClient
@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableSwagger2
@EnableTransactionManagement
public class TokenSellConsoleBootstrap {
    public static void main(String[] args) {
        new SpringApplicationBuilder(TokenSellConsoleBootstrap.class).web(true).run(args);
    }
}
