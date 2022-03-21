package com.wch.gulimall.product.service.impl;

import com.wch.gulimall.product.entity.SkuImagesEntity;
import com.wch.gulimall.product.entity.SpuInfoDescEntity;
import com.wch.gulimall.product.service.*;
import com.wch.gulimall.product.vo.web.SkuItemVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.Query;

import com.wch.gulimall.product.dao.SkuInfoDao;
import com.wch.gulimall.product.entity.SkuInfoEntity;


/**
 * @author WCH
 */
@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * sku的基本信息
     *
     * @param skuInfoEntity
     */
    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        //key:
        //catelogId: 0
        //brandId: 0 min: 0
        //max: 0
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and((w -> {
                queryWrapper.eq("sku_id", key).or().like("sku_name", key);
            }));
        }
        String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotBlank(catelogId) && !"0".equalsIgnoreCase(catelogId)) {

            queryWrapper.eq("catelog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotBlank(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if (StringUtils.isNotBlank(min)) {
            queryWrapper.ge("price", min);
        }
        String max = (String) params.get("max");

        if (StringUtils.isNotBlank(max)) {
            try {

                BigDecimal bigDecimal = new BigDecimal(max);
                if (bigDecimal.compareTo(new BigDecimal("0")) == 1) {
                    queryWrapper.le("price", max);
                }
            } catch (Exception e) {

            }
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkuInfoById(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    /**
     * 查询商品详情
     *
     * @param skuId
     * @return
     */
    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();
        //异步编排
        CompletableFuture<SkuInfoEntity> infoEntityCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //sku的基本信息
            SkuInfoEntity skuInfoEntity = this.getById(skuId);
            skuItemVo.setSkuInfoEntity(skuInfoEntity);
            return skuInfoEntity;
        }, threadPoolExecutor);
        CompletableFuture<Void> saleFuture = infoEntityCompletableFuture.thenAcceptAsync((result) -> {
            //获取spu的销售属性组合
            List<SkuItemVo.SkuItemSaleVo> skuItemSaleVos = skuSaleAttrValueService.getSaleAttrsBySpuId(result.getSpuId());
            skuItemVo.setSkuItemSaleVos(skuItemSaleVos);
        }, threadPoolExecutor);

        CompletableFuture<Void> descFuture = infoEntityCompletableFuture.thenAcceptAsync(res -> {
            //获取spu的介绍
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setSpuInfoDescEntity(spuInfoDescEntity);
        }, threadPoolExecutor);
        CompletableFuture<Void> baseAttrFuture = infoEntityCompletableFuture.thenAcceptAsync(res -> {
            //获取spu规格参数信息
            List<SkuItemVo.SpuItemBaseAttrVo> spuItemBaseAttrVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatelogId());
            skuItemVo.setGroupAttrs(spuItemBaseAttrVos);
        }, threadPoolExecutor);

        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            //sku的图片信息
            List<SkuImagesEntity> imagesEntities = skuImagesService.getImagesById(skuId);
            skuItemVo.setImages(imagesEntities);
        }, threadPoolExecutor);
        //等待任务完成
        CompletableFuture.allOf(saleFuture, descFuture, baseAttrFuture, imagesFuture).get();
        return skuItemVo;
    }

}