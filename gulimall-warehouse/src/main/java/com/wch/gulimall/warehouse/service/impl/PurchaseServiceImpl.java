package com.wch.gulimall.warehouse.service.impl;

import com.wch.common.constant.WareConstant;
import com.wch.gulimall.warehouse.entity.PurchaseDetailEntity;
import com.wch.gulimall.warehouse.service.PurchaseDetailService;
import com.wch.gulimall.warehouse.service.WareSkuService;
import com.wch.gulimall.warehouse.vo.MergeVo;
import com.wch.gulimall.warehouse.vo.PurchaseFinishVo;
import com.wch.gulimall.warehouse.vo.PurchaseItemDoneVo;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wch.common.utils.PageUtils;
import com.wch.common.utils.Query;

import com.wch.gulimall.warehouse.dao.PurchaseDao;
import com.wch.gulimall.warehouse.entity.PurchaseEntity;
import com.wch.gulimall.warehouse.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 未领取的采购单
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryUnreceivePage(Map<String, Object> params) {


        QueryWrapper<PurchaseEntity> queryWrapper = new QueryWrapper<PurchaseEntity>()
                .eq("status", 0).or().eq("status", 1);

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        //没有提交id就新建一个采购单
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        } else {
            //TODO 确认采购单的状态，新建或者是待分配才可以进行合并整单
            List<Long> items = mergeVo.getItems();
            Long finalPurchaseId = purchaseId;
            List<PurchaseDetailEntity> collect = items.stream().map(i -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(i);
                purchaseDetailEntity.setPurchaseId(finalPurchaseId);
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect);
            //更新时间和id
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(purchaseId);
            purchaseEntity.setUpdateTime(new Date());
            this.updateById(purchaseEntity);
        }
    }

    /**
     * 领取采购单
     *
     * @param ids 采购单id
     */
    @Override
    public void received(List<Long> ids) {
        //确认当前采购单是新建或已分配状态
        List<PurchaseEntity> collect = ids.stream().map(this::getById).filter(item -> {
                    if (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                            item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                        return true;
                    }
                    return false;
                }

        ).map(item1 -> {
            //更新采购单状态
            item1.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getCode());
            item1.setUpdateTime(new Date());
            return item1;
        }).collect(Collectors.toList());
        //改变采购单的状态
        this.updateBatchById(collect);
        //改变采购项状态 （采购需求）
        collect.forEach((i -> {
            List<PurchaseDetailEntity> list = purchaseDetailService.listDetailByPurchaseId(i.getId());
            List<PurchaseDetailEntity> collect1 = list.stream().map(entity -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(entity.getId());
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect1);
        }));
    }

    @Override
    public boolean purchaseAssign(PurchaseEntity purchase) {
        // 先更新采购单，这个采购单单位状态必须是新建，才能分配
        if (purchase.getStatus() != null && (purchase.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode())) {
            purchase.setStatus(WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());
            this.updateById(purchase);
            // 再更新此采购单上的采购项状态为已分配
            List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.list(new QueryWrapper<PurchaseDetailEntity>().in("purchase_id", purchase.getId()));
            if (!CollectionUtils.isEmpty(purchaseDetailEntities)) {
                List<PurchaseDetailEntity> detailEntityList = purchaseDetailEntities.stream().map(detail -> {
                    PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                    detailEntity.setId(detail.getId());
                    detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                    return detailEntity;
                }).collect(Collectors.toList());
                purchaseDetailService.updateBatchById(detailEntityList);
            }
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void done(PurchaseFinishVo purchaseFinishVo) {

        Long id = purchaseFinishVo.getId();
        //改变采购项状态
        boolean flag = true;
        List<PurchaseItemDoneVo> purchaseFinishVoItems = purchaseFinishVo.getItems();
        ArrayList<PurchaseDetailEntity> purchaseDetailEntities = new ArrayList<>();
        for (PurchaseItemDoneVo purchaseItemDoneVo : purchaseFinishVoItems) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if (purchaseItemDoneVo.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
                flag = false;
                purchaseDetailEntity.setStatus(purchaseItemDoneVo.getStatus());
            }else {
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISHED.getCode());
                //将成功采购的入库
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(purchaseItemDoneVo.getItemId());
                wareSkuService.addStock(detailEntity.getSkuId(), detailEntity.getWareId(), detailEntity.getSkuNum());
            }
            purchaseDetailEntity.setId(purchaseItemDoneVo.getItemId());
            purchaseDetailEntities.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(purchaseDetailEntities);
        //改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag?WareConstant.PurchaseStatusEnum.FINISHED.getCode() : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        this.updateById(purchaseEntity);
    }

}