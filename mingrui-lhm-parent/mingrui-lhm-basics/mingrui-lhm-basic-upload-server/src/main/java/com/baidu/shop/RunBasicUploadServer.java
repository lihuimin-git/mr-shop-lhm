package com.baidu.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class RunBasicUploadServer {
    public static void main(String[] args) {
        SpringApplication.run(RunBasicUploadServer.class);
    }
}