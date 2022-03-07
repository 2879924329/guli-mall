package com.wch.gulimall.product.vo;

import lombok.Data;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/3 20:57
 */
@Data
public class AttrResponseVo extends AttrVo{
    /**
     * 所属的分类名
     */
    private String catelogName;
    /**
     * 所属的分组名
     */
    private String groupName;
    /**
     * 完整的分类路径
     */
    private Long[] catelogPath;
}
