package com.rzsd.wechat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@MapperScan("com.rzsd.wechat.common.mapper")
@ServletComponentScan(basePackages = "com.rzsd.wechat")
public class RzsdApplication {

    public static void main(String[] args) {
        SpringApplication.run(RzsdApplication.class, args);
    }
}
