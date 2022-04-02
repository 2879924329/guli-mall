package com.wch.gulimall.warehouse.vo;

import com.wch.gulimall.warehouse.to.MemberAddressTo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/2 20:09
 */
@Data
public class FareVo {
    private MemberAddressTo address;
    private BigDecimal fare;
}
