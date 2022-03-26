package com.wch.gulimall.product.vo.web;

import com.wch.gulimall.product.vo.Attr;
import lombok.Data;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/26 22:27
 */
@Data
public class SpuItemAttrGroupVo {

    private String groupName;

    private List<Attr> attrs;
}
