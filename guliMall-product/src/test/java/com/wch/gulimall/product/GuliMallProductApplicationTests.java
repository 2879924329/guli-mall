package com.wch.gulimall.product;

import com.wch.gulimall.product.entity.BrandEntity;
import com.wch.gulimall.product.service.BrandService;
import com.wch.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class GuliMallProductApplicationTests {


    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;
    @Test
    void categoryTest(){
        Long[] cateLogPath = categoryService.findCateLogPath(396L);
        log.info("完整路径：", Arrays.asList(cateLogPath));
    }
    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("小米");
        brandService.save(brandEntity);
        System.out.println("保存成功");
    }

    @Test
    void redisTest(){
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        stringStringValueOperations.set("hello", "hello");
        String hello = stringStringValueOperations.get("hello");
        System.out.println(hello);

    }

    @Test
    void redissonTest(){
        System.out.println(redissonClient);
    }


}
