package com.wch.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author WCH
 * <p>
 * 整合mp， 已经导入通用的工具模块
 * <p>
 * 配置数据源
 * 1, 导入mysql驱动
 * 配置mp
 * 1, 使用@MapperScan
 * 2，告诉mp sql映射文件在哪
 * <p>
 * 模板引擎：
 * themeleaf-starter :关闭缓存
 * 静态资源都放在static文件夹下面
 * <p>
 * 页面修改实时更新， 引入dev-tools
 * 修改完页面， ctrl + shift + F9
 * <p>
 * 整合redis
 * <p>
 * 整合redisson作为分布式锁等功能的框架
 * 引入redisson（这个依赖不是场景启动器的依赖，引入这个学redis的配置）
 * <!--redisson-->
 * <!-- https://mvnrepository.com/artifact/org.redisson/redisson -->
 * <dependency>
 * <groupId>org.redisson</groupId>
 * <artifactId>redisson</artifactId>
 * <version>${redisson.version}</version>
 * </dependency>
 * <p>
 * 整合springcache简化缓存开发
 * 1，引入依赖 spring-boot-starter-cache
 * 2, 写配置
     * 1）自动配置了哪些：CacheAutoConfiguration会导入RedisCacheConfiguration
     * 自动配置了缓存管理器RedisCacheManager
     * 2)我们需要配置使用redis作为缓存
     * 3）使用注解
     * @Cacheable ：主要针对方法配置，能够根据方法的请求参数对其结果进行缓存
     * @CacheEvict ：清空缓存
     * @CachePut ：保证方法被调用，又希望结果被缓存。
     * @CacheConfig ： 在类级别共享缓存的相同配置
     * @Caching : 组合以上多个操作
 * 3, 开启缓存功能：@EnableCaching
 * 4,原理：CacheAutoConfiguration -> RedisCacheConfiguration -> 自动配置了RedisCacheManager
 *     初始化所有的缓存 -> 每个缓存都决定使用什么配置 -> 如果redisCacheConfiguration有就用自己的，没有
 *     就使用默认配置，只需要在容器中放一个RedisCacheConfiguration即可。 -> 就会应用到当前RedisCacheManager
 *     管理的所有缓存分区中
 * 5， springCache的不足：
 *      1）读模式，
 *          缓存穿透，查询一个null数据，                   解决：缓存一个空数据：   cache-null-values: true
 *          缓存击穿，大量并发进来查询一个一个正好过期的数据，  解决：加锁（默认底层没有加锁）,@Cacheable 中使用Cacheable解决缓存击穿问题，家的锁也不是分布式锁，是本地锁
 *          缓存雪崩，大量的key同时过期                     解决：加随机时间 加过期时间
 *       2）写模式 （缓存一致性）
 *          读写加锁
 *          引入中间件Canal，感知mysql的更新去更新数据库
 *          读多写少 直接去数据库查询就好
 *      总结：常规的数据（读多写少，即时性，一致性要求不高的）的缓存 完全可以使用SpringCache，写模式下，只要缓存的数据有过期时间就足够了
 *            特殊数据特殊设置
 *
 *
 * @EnableRedisHttpSession 的核心原理 导入了RedisHttpSessionConfiguration,
 *  
 *
 */
//@EnableCaching（移步到配置类）
@EnableRedisHttpSession
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
 */





