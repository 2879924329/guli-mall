package com.wch.gulimall.secondkill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 整合sentinel，导入依赖
 * 下载sentinel控制台， 在控制台调控有的参数，默认服务关闭就失效，重启后需要重新配置
 * 配置yml,
 *
 * 2， 每一个微服务都应该导入审计模块 配置：management:
                                           endpoints:
                                              web:
                                                exposure:
                                                 include: '*'

 3，自定义sentinel的流控返回

 4, 使用sentienl来保护feign的远程调用。

 try(Entry entry = SphU.entry("queryCurrentSecondKillSkus"))自定义资源
 基于注解： @SentinelResource
 */


@EnableRedisHttpSession
@EnableFeignClients("com.wch.gulimall.secondkill.feign")
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GulimallSecondkillApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallSecondkillApplication.class, args);
    }

}
