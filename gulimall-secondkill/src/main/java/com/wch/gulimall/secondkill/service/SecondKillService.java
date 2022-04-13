package com.wch.gulimall.secondkill.service;


import com.wch.gulimall.secondkill.to.SecondKillRedisEntityTo;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/10 21:33
 */

public interface SecondKillService {
    void upSecondKill();

    List<SecondKillRedisEntityTo> queryCurrentSecondKillSkus();

    SecondKillRedisEntityTo querySecKillSkuInfo(Long skuId);

    String secondKill(String killId, String key, Integer num);
}
