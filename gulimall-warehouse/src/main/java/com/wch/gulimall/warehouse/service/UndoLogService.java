package com.wch.gulimall.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wch.common.utils.PageUtils;
import com.wch.gulimall.warehouse.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author wch
 * @email 2879924329@qq.com
 * @date 2022-02-20 22:27:21
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

