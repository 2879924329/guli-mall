package com.wch.gulimall.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wch.common.utils.PageUtils;
import com.wch.gulimall.warehouse.entity.WareInfoEntity;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 22:27:21
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    BigDecimal getFare(Long addrId);
}

