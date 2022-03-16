package com.wch.gulimall.search.vo;

import com.wch.common.to.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/15 21:50
 */
@Data
public class SearchResponse {
    /**
     * es中查询到的商品信息
     */
    private List<SkuEsModel> products;
    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 只记录
     */
    private Long total;
    /**
     * 总页码
     */
    private Integer totalPages;

    private List<BrandVo> brandVos;

    private List<catalogVo> catalogVos;

    private List<AttrVo> attrVos;

    @Data
    private static class BrandVo{
        private Integer brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    private static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValues;
    }
    @Data
    private static class catalogVo{
        private Long catalogId;
        private String catalogName;
    }
}
