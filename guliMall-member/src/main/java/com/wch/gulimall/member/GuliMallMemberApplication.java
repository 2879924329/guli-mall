package com.wch.gulimall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author WCH
 */
@EnableRedisHttpSession
@SpringBootApplication
@EnableFeignClients(basePackages = "com.wch.gulimall.member.feign")
@EnableDiscoveryClient
public class GuliMallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliMallMemberApplication.class, args);
    }

}
