package com.wch.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/13 22:23
 * <p>
 * 学习redis配置类
 */
@Configuration
public class RedisConfig {
    /**
     * 所有对redisson使用都是通过RedissonClient对象
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://101.200.200.220:6379");
        return Redisson.create(config);
    }
}
