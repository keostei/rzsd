package com.rzsd.wechat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.rzsd.wechat.listen.PropertiesListener;

@SpringBootApplication
@MapperScan("com.rzsd.wechat.common.mapper")
@ServletComponentScan(basePackages = "com.rzsd.wechat")
@EnableScheduling
public class RzsdApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RzsdApplication.class);
        application.addListeners(new PropertiesListener("wechat-message.properties"));
        application.run(args);
    }
}
