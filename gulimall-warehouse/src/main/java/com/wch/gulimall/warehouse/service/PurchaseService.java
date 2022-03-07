package com.wch.gulimall.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wch.common.utils.PageUtils;
import com.wch.gulimall.warehouse.entity.PurchaseEntity;
import com.wch.gulimall.warehouse.vo.MergeVo;
import com.wch.gulimall.warehouse.vo.PurchaseFinishVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 22:27:21
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryUnreceivePage(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void received(List<Long> ids);
    /**
     * 将新建的采购单分配给采购人员
     * @param purchase
     * @return
     */
    boolean purchaseAssign(PurchaseEntity purchase);
    void done(PurchaseFinishVo purchaseFinishVo);
}

