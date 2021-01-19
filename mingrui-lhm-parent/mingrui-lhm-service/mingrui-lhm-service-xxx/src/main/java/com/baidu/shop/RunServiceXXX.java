package com.baidu.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.baidu.shop.mapper")
public class RunServiceXXX {
    public static void main(String[] args) {
        SpringApplication.run(RunServiceXXX.class);
    }
}
