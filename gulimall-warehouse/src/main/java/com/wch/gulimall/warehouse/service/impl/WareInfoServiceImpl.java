package com.wch.gulimall.warehouse.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.wch.common.utils.R;
import com.wch.gulimall.warehouse.feign.MemberFeignService;
import com.wch.gulimall.warehouse.to.MemberAddressTo;
import com.wch.gulimall.warehouse.vo.FareVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.Query;

import com.wch.gulimall.warehouse.dao.WareInfoDao;
import com.wch.gulimall.warehouse.entity.WareInfoEntity;
import com.wch.gulimall.warehouse.service.WareInfoService;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {


    @Autowired
    private MemberFeignService memberFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            queryWrapper.eq("id", key)
                    .or().like("name", key)
                    .or().like("address", key)
                    .or().like("areacode",key);
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),queryWrapper
        );

        return new PageUtils(page);
    }

    /**
     * 根据收获地址计算运费
     * @param addrId
     * @return
     */
    @Override
    public FareVo getFare(Long addrId) {
        R addrInfo = memberFeignService.addrInfo(addrId);
        FareVo fareVo = new FareVo();
        Random random = new Random();
        MemberAddressTo addrInfoData = addrInfo.getData("memberReceiveAddress", new TypeReference<MemberAddressTo>() {});
        if (!ObjectUtils.isEmpty(addrInfoData)){
            //运费随机取一个
            int nextInt = random.nextInt(20);
            BigDecimal fare = new BigDecimal(nextInt);
            fareVo.setFare(fare);
            fareVo.setAddress(addrInfoData);
            return fareVo;
        }
        return null;
    }

}