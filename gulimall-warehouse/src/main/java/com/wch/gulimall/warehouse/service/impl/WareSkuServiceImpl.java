package com.wch.gulimall.warehouse.service.impl;

import com.wch.common.utils.R;
import com.wch.gulimall.warehouse.feign.ProductFeignService;
import com.wch.gulimall.warehouse.vo.SkuHasStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.Query;

import com.wch.gulimall.warehouse.dao.WareSkuDao;
import com.wch.gulimall.warehouse.entity.WareSkuEntity;
import com.wch.gulimall.warehouse.service.WareSkuService;
import org.springframework.util.StringUtils;


/**
 * @author WCH
 */
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!org.springframework.util.StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1, 判断如果没有这个库存记录，则直接插入
        Integer count = baseMapper.selectCount(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (count > 0) {
            this.baseMapper.addStock(skuId, wareId, skuNum);
        } else {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //远程调用查skuName
            //调用失败，事务无需回滚
            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> map = (Map<String, Object>) info.get("skuInfo");
                if (info.getCode() == 0) {
                 wareSkuEntity.setSkuName((String) map.get("skuName"));
                }
            } catch (Exception e) {

            }
            this.baseMapper.insert(wareSkuEntity);
        }
    }

    /**
     * 查询库存
     * @param skuIds
     * @return
     */
    @Override
    public List<SkuHasStockVo> getSkuStock(List<Long> skuIds) {
        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            //查询库存总量
            //   select sum(stock - stock_locked) from wms_ware_sku where sku_id = #{skuId}
            Long count = baseMapper.getSkuStock(skuId);
            skuHasStockVo.setSkuId(skuId);
            skuHasStockVo.setHasStock(count != null && count > 0);
            return skuHasStockVo;
        }).collect(Collectors.toList());
        return collect;
    }

}