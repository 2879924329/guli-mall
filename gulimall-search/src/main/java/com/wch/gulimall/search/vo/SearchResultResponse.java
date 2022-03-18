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
public class SearchResultResponse {
    /**
     * es中查询到的商品信息
     */
    private List<SkuEsModel> products;
    /**
     * 总共几页
     */
    private Long totalPage;

    /**
     * 当前页码
     */
    private Integer currPage;

    /**
     * 每页显示几个
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long totalCount;


    private List<BrandVo> brandVos;

    private List<CatalogVo> catalogVos;

    private List<AttrVo> attrVos;

    @Data
    public static class BrandVo{
        private long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValues;
    }
    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }
}
