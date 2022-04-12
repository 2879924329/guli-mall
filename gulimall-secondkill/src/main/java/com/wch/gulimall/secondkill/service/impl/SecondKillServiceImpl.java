package com.wch.gulimall.secondkill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wch.common.utils.R;
import com.wch.gulimall.secondkill.feign.CouponFeignService;
import com.wch.gulimall.secondkill.feign.ProductFeignService;
import com.wch.gulimall.secondkill.service.SecondKillService;
import com.wch.gulimall.secondkill.to.SecondKillRedisEntityTo;
import com.wch.gulimall.secondkill.to.SecondKillRelationEntityTo;
import com.wch.gulimall.secondkill.to.SecondSessionEntityTo;
import com.wch.gulimall.secondkill.to.SkuInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/10 21:33
 */
@Slf4j
@Service
public class SecondKillServiceImpl implements SecondKillService {

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private RedissonClient redissonClient;

    private final String SECKILL_SESSIONS_PREFIX = "seckill:sessions:";

    private final String SKU_KILL_CACHE_PREFIX = "seckill:skus";

    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";

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
            saveSessionInfo(data);
        }
    }


    /**
     * 获取当前可以参与秒杀的商品信息
     * @return
     */
    @Override
    public List<SecondKillRedisEntityTo> queryCurrentSecondKillSkus() {
        //1，确定当前时间属于哪个秒杀场次
        long time = new Date().getTime();
        //查到所有的keys
        Set<String> keys = stringRedisTemplate.keys(SECKILL_SESSIONS_PREFIX + "*");
        if (!CollectionUtils.isEmpty(keys)){
            for (String key : keys) {
                //key seckill:sessions:1649606400000_1649692800000
                String replace = key.replace(SECKILL_SESSIONS_PREFIX, "");
                String[] s = replace.split("_");
                long startTime = Long.parseLong(s[0]);
                long endTime = Long.parseLong(s[1]);
                if (time >= startTime && time <= endTime){
                    //2，获取这个秒杀场次的所有商品信息
                    List<String> list = stringRedisTemplate.opsForList().range(key, -100, 100);
                    if (!CollectionUtils.isEmpty(list)){
                        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKU_KILL_CACHE_PREFIX);
                        if (!CollectionUtils.isEmpty(list)){
                            List<String> multiGet = hashOps.multiGet(list);
                            if (!CollectionUtils.isEmpty(multiGet)){
                                return multiGet.stream().map(item -> {
                                    //当前秒杀开始了，需要随机码
                                    return JSON.parseObject(item, SecondKillRedisEntityTo.class);
                                }).collect(Collectors.toList());
                            }
                        }
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }

    @Override
    public SecondKillRedisEntityTo querySecKillSkuInfo(Long skuId) {
        // 1.匹配查询当前商品的秒杀信息
        BoundHashOperations<String, String, String> skuOps = stringRedisTemplate.boundHashOps(SKU_KILL_CACHE_PREFIX);
        // 获取所有商品的key：sessionId-skuId
        Set<String> keys = skuOps.keys();
        if (!CollectionUtils.isEmpty(keys)) {
            String lastIndex = "-" + skuId;
            for (String key : keys) {
                //返回此字符串中最后一次出现指定子字符串的索引。
                // 空字符串“”的最后一次出现被认为出现在索引值 this.length() 处。
                // 返回的索引是最大值 k 其中： this.startsWith(str, k)
                // 如果不存在这样的 k 值，则返回 -1。
                // 参数： str - 要搜索的子字符串。
                // 返回： 指定子字符串最后一次出现的索引，
                // 如果没有出现，则返回 -1。
                if (key.lastIndexOf(lastIndex) > -1) {
                    // 商品id匹配成功
                    String jsonString = skuOps.get(key);
                    // 进行序列化
                    SecondKillRedisEntityTo skuInfo = JSON.parseObject(jsonString, SecondKillRedisEntityTo.class);
                    long currentTime = System.currentTimeMillis();
                    assert skuInfo != null;
                    Long endTime = skuInfo.getEndTime();
                    if (currentTime <= endTime) {
                        // 当前时间小于截止时间
                        Long startTime = skuInfo.getStartTime();
                        if (currentTime >= startTime) {
                            // 返回当前正处于秒杀的商品信息
                            return skuInfo;
                        }
                        // 返回预告信息，不返回随机码
                        skuInfo.setRandomCode(null);// 随机码
                        return skuInfo;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 保存当前活动信息
     * @param secondSessionEntityTo
     */
    private void saveSessionTo(List<SecondSessionEntityTo> secondSessionEntityTo){
        secondSessionEntityTo.forEach(secondSessionEntityTo1 -> {
            long startTime = secondSessionEntityTo1.getStartTime().getTime();
            long endTime = secondSessionEntityTo1.getEndTime().getTime();
            String key = SECKILL_SESSIONS_PREFIX + startTime + "_" + endTime;
            //判断是否已经上架
            Boolean hasKey = stringRedisTemplate.hasKey(key);
            if (Boolean.FALSE.equals(hasKey)){
                List<SecondKillRelationEntityTo> relationEntities = secondSessionEntityTo1.getRelationEntities();
                if (!CollectionUtils.isEmpty(relationEntities)){
                    List<String> collect = relationEntities.stream().map(item -> item.getPromotionSessionId().toString() + "-" + item.getSkuId().toString()).collect(Collectors.toList());
                    //key :场次id+商品skuId
                    stringRedisTemplate.opsForList().leftPushAll(key, collect);
                }else {
                    log.warn("暂时还未关联商品！");
                }
            }
        });
    }

    private void saveSessionInfo(List<SecondSessionEntityTo> secondSessionEntityTo){
        secondSessionEntityTo.forEach(secondSessionEntityTo1 -> {
            BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = stringRedisTemplate.boundHashOps(SKU_KILL_CACHE_PREFIX);
            secondSessionEntityTo1.getRelationEntities().forEach(secondKillRelationEntityTo -> {
                //秒杀随机码
                String token = UUID.randomUUID().toString().replace("-", "");
                //key :场次id+商品skuId
                String key = secondKillRelationEntityTo.getPromotionSessionId().toString() + "-" + secondKillRelationEntityTo.getSkuId().toString();
                if (Boolean.FALSE.equals(stringObjectObjectBoundHashOperations.hasKey(key))){
                    SecondKillRedisEntityTo secondKillRedisEntityTo = new SecondKillRedisEntityTo();
                    //sku的基本数据
                    R r = productFeignService.getSkuInfo(secondKillRelationEntityTo.getSkuId());
                    if (r.getCode() == 0){
                        SkuInfoTo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoTo>() {
                        });
                        secondKillRedisEntityTo.setSkuInfoTo(skuInfo);
                    }
                    //sku的秒杀信息
                    BeanUtils.copyProperties(secondKillRelationEntityTo, secondKillRedisEntityTo);
                    //设置开始和结束时间
                    secondKillRedisEntityTo.setStartTime(secondSessionEntityTo1.getStartTime().getTime());
                    secondKillRedisEntityTo.setEndTime(secondSessionEntityTo1.getEndTime().getTime());
                    secondKillRedisEntityTo.setRandomCode(token);
                    String toJSONString = JSON.toJSONString(secondKillRedisEntityTo);
                    stringObjectObjectBoundHashOperations.put(key, toJSONString);

                    //如果当前场次商品库存信息已经上架，就不需要上架
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    //商品可以秒杀的库存作为分布式的信号量 --限流
                    semaphore.trySetPermits(secondKillRelationEntityTo.getSeckillCount());
                }
            });
        });
    }
}
