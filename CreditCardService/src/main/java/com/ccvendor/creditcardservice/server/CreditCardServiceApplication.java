package com.ccvendor.creditcardservice.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@EnableEurekaClient
@ComponentScan(basePackages = "com.ccvendor.creditcardservice")
@EnableJpaRepositories("com.ccvendor.creditcardservice.repository")
@EntityScan("com.ccvendor.creditcardservice.model")
public class CreditCardServiceApplication {

    public static void main(final String[] args) {
        SpringApplication.run(CreditCardServiceApplication.class, args);
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
