package com.wch.gulimall.secondkill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wch.common.utils.R;
import com.wch.gulimall.secondkill.feign.CouponFeignService;
import com.wch.gulimall.secondkill.service.SecondKillService;
import com.wch.gulimall.secondkill.to.SecondKillRedisEntityTo;
import com.wch.gulimall.secondkill.to.SecondKillRelationEntityTo;
import com.wch.gulimall.secondkill.to.SecondSessionEntityTo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/10 21:33
 */
public class SecondKillServiceImpl implements SecondKillService {

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final String PREFIX = "seckill:sessions:";

    private final String SKU_KILL_CACHE_PREFIX = "seckill:skus";

    @Override
    public void upSecondKill() {
        //1,去数据库扫描最近三天需要参与秒杀的活动
        R r = couponFeignService.getLastSessions();
        if (r.getCode() == 0){
            List<SecondSessionEntityTo> data = r.getData(new TypeReference<List<SecondSessionEntityTo>>() {
            });
            //缓存到redis
            // 1,缓存活动信息
            saveSessionTo(data);
            //2，缓存关联的商品信息
        }
    }

    /**
     * 保存当前活动信息
     * @param secondSessionEntityTo
     */
    private void saveSessionTo(List<SecondSessionEntityTo> secondSessionEntityTo){
        secondSessionEntityTo.forEach(secondSessionEntityTo1 -> {
            long startTime = secondSessionEntityTo1.getStartTime().getTime();
            long endTime = secondSessionEntityTo1.getEndTime().getTime();
            String key = PREFIX + startTime + "_" + endTime;
            List<String> collect = secondSessionEntityTo1.getRelationEntities().stream().map(item -> item.getSkuId().toString()).collect(Collectors.toList());
            stringRedisTemplate.opsForList().leftPushAll(key, collect);
        });
    }

    private void saveSessionInfo(List<SecondSessionEntityTo> secondSessionEntityTo){
        secondSessionEntityTo.forEach(secondSessionEntityTo1 -> {
            BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = stringRedisTemplate.boundHashOps(SKU_KILL_CACHE_PREFIX);
            secondSessionEntityTo1.getRelationEntities().forEach(secondKillRelationEntityTo -> {
                String toJSONString = JSON.toJSONString(secondKillRelationEntityTo);
                SecondKillRedisEntityTo secondKillRedisEntityTo = new SecondKillRedisEntityTo();
                //sku的基本数据

                //sku的秒杀信息
                BeanUtils.copyProperties(secondKillRelationEntityTo, secondKillRedisEntityTo);
                stringObjectObjectBoundHashOperations.put(secondKillRelationEntityTo.getId(), toJSONString);
            });
        });
    }
}
