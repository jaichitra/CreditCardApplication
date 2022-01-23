package com.ccvendor.appgateway.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableEurekaClient
@SpringBootApplication
@EnableZuulProxy
//@ComponentScan(basePackages = "com.ccvendor.appgateway")
public class CardApplicationGatewayApplication {

    public static void main(final String[] args) {
        SpringApplication.run(CardApplicationGatewayApplication.class, args);
    }
}
