package com.wch.gulimall.order.vo;

import com.wch.common.to.MemberEntityTo;
import com.wch.gulimall.order.to.MemberAddressTo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/2 21:51
 */
@Data
public class FareVo {
    private MemberAddressTo address;
    private BigDecimal fare;
}
