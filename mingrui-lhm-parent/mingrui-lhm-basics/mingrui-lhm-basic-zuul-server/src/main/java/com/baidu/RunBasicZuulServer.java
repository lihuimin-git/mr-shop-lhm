package com.baidu;

import org.apache.commons.lang3.builder.EqualsExclude;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class RunBasicZuulServer {
    public static void main(String[] args) {
        SpringApplication.run(RunBasicZuulServer.class);
    }
}
