package com.wch.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author WCH
 *
 * 整合mp， 已经导入通用的工具模块
 *
 * 配置数据源
 *  1, 导入mysql驱动
 * 配置mp
 * 1, 使用@MapperScan
 * 2，告诉mp sql映射文件在哪
 *
 */
@EnableFeignClients(basePackages = "com.wch.gulimall.product.feign")
@MapperScan(basePackages = "com.wch.gulimall.product.dao")
@SpringBootApplication
@EnableDiscoveryClient

public class GuliMallProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuliMallProductApplication.class, args);
    }
}
/**
 * 逻辑删除：
 * 1）配置全局的逻辑删除规则（yml）  （省略）
 * 2）配置逻辑删除的组件            （省略）
 * 3）给实体类每某个字段加上逻辑删除的注解
 *
 *
 */