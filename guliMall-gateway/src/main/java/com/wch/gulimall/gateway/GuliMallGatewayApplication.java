package com.wch.gulimall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * @author WCH
 *
 * 网管需要开启服务注册发现
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
public class GuliMallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliMallGatewayApplication.class, args);
    }

}
