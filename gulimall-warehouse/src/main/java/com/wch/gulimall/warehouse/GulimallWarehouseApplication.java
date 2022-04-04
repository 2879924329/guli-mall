package com.wch.gulimall.warehouse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author WCH
 */
@EnableRabbit
@MapperScan("com.wch.gulimall.warehouse.dao")
@EnableDiscoveryClient
@EnableTransactionManagement
@SpringBootApplication
@EnableFeignClients("com.wch.gulimall.warehouse.feign")
public class GulimallWarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallWarehouseApplication.class, args);
    }

}
