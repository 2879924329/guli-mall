package com.wch.gulimall.coupon.dao;

import com.wch.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 21:58:00
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
