package com.wly.chatgpt.data;


import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @description 启动类
 */
@SpringBootApplication
@Configurable
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class);
    }

}
