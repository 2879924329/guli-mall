package com.wch.gulimall.coupon.service.impl;

import com.wch.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.wch.gulimall.coupon.service.SeckillSkuRelationService;
import org.checkerframework.checker.units.qual.min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.Query;

import com.wch.gulimall.coupon.dao.SeckillSessionDao;
import com.wch.gulimall.coupon.entity.SeckillSessionEntity;
import com.wch.gulimall.coupon.service.SeckillSessionService;
import org.springframework.util.CollectionUtils;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {


    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询最近三天需要参与秒杀的活动以及关联的商品
     * @return
     */
    @Override
    public List<SeckillSessionEntity> getLastSessions() {
        //计算最近三天
        //2022-04-09 00:00:00 - 2022-04-12 23:59:59
        List<SeckillSessionEntity> seckillSessionEntities = list(new QueryWrapper<SeckillSessionEntity>().between("start_time", getStartTime(), getEndTime()));
        if (!CollectionUtils.isEmpty(seckillSessionEntities)){
            return seckillSessionEntities.stream().map(seckillSessionEntity -> {
                Long id = seckillSessionEntity.getId();
                List<SeckillSkuRelationEntity> seckillSkuRelationEntities = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", id));
                seckillSessionEntity.setRelationEntities(seckillSkuRelationEntities);
                return seckillSessionEntity;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 起始时间
     * @return 2022-04-09 00:00:00
     */
    private static String getStartTime(){
        LocalDate now = LocalDate.now();
        LocalTime min = LocalTime.MIN;
        return LocalDateTime.of(now, min).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 结束时间
     * @return 2022-04-12 23:59:59
     */
    private static String getEndTime(){
        LocalDate now = LocalDate.now();
        LocalDate plusDays = now.plusDays(2);
        LocalTime max = LocalTime.MAX;
            return LocalDateTime.of(plusDays, max).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static void main(String[] args) {
        System.out.println(getStartTime());
        System.out.println(getEndTime());
    }

}