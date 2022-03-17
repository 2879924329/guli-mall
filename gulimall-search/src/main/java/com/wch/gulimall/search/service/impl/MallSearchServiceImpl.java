package com.wch.gulimall.search.service.impl;

import com.wch.gulimall.search.config.ElasticSearchConfig;
import com.wch.gulimall.search.constant.EsConstant;
import com.wch.gulimall.search.service.MallSearchService;
import com.wch.gulimall.search.vo.SearchParamVo;
import com.wch.gulimall.search.vo.SearchResultResponse;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/15 21:15
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    /**
     * 商品前台检索，自动将页面传过来的请求参数封装成对象
     * @param searchParamVo 搜索栏传来的参数
     * @return 根据页面传递过来的信息去检索返回结果
     */
    @Override
    public SearchResultResponse search(SearchParamVo searchParamVo) {
        SearchResultResponse searchResultResponse = null;
        //1, 动态构建查询需要的dsl语句
        //2， 准备检索条件
        SearchRequest searchRequest = buildSearchRequest(searchParamVo);
        try {
            //3， 执行检索要求
            org.elasticsearch.action.search.SearchResponse searchResponse = restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
            //4， 分析响应数据封装成我们需要的格式
            searchResultResponse = buildSearchResult(searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 构建结果数据
     * @return
     * @param searchResponse
     */
    private SearchResultResponse buildSearchResult(SearchResponse searchResponse) {
        return null;
    }

    /**
     * 构建请求条件
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParamVo searchParamVo){
        //动态构建查询需要的dsl语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建bool query
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //1.1 must
        if (!StringUtils.isEmpty(searchParamVo.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", searchParamVo.getKeyword()));
        }
        //1.2 filter
        if (searchParamVo.getCatalog3Id() != null){
            //按照三级分类id查询
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", searchParamVo.getCatalog3Id() ));
        }
        //1.3 filter
        if (!CollectionUtils.isEmpty(searchParamVo.getBrandId())){
            //按照品牌id查询
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", searchParamVo.getBrandId()));
        }
        //1.4 按照所有指定的属性查
           //1.4.1 按照是否有库存查询
        // 2.4 库存
        if (searchParamVo.getHasStock() != null) {
            boolean flag = searchParamVo.getHasStock() != 0;
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", flag));
        }
           //按照价格区间查询
        if (!StringUtils.isEmpty(searchParamVo.getSkuPrice())){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] split = searchParamVo.getSkuPrice().split("_");
            if (split.length == 2){
                //在某个区间 500 - 1000
                rangeQuery.gte(split[0]).lte(split[1]);
            }else if (split.length == 1){
                if (searchParamVo.getSkuPrice().startsWith("_")){
                    rangeQuery.lte(split[0]);
                }
                if(searchParamVo.getSkuPrice().endsWith("_")){
                 rangeQuery.gte(split[0]);
                }
            }
            boolQueryBuilder.filter(rangeQuery);
        }
        //按照指定属性查询
        if (!CollectionUtils.isEmpty(searchParamVo.getAttrs())){
            for (String attr : searchParamVo.getAttrs()) {
                BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                //属性id
                String attrId = s[0];
                //属性检索用的值
                String[] attrValues = s[1].split(":");
                nestedBoolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBoolQueryBuilder.must(QueryBuilders.termsQuery("attr.attrValue", attrValues));
                //每一个都必须生成一个嵌入式的查询
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQueryBuilder, ScoreMode.None);
                boolQueryBuilder.filter(nestedQuery);
            }
        }
        searchSourceBuilder.query(boolQueryBuilder);

        //排序
        if (!StringUtils.isEmpty(searchParamVo.getSort())){
            String sort = searchParamVo.getSort();
            String[] s = sort.split("_");
            SortOrder sortOrder = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC:SortOrder.DESC;
            searchSourceBuilder.sort(s[0], sortOrder);
        }
        //分页, from = (pageNum - 1) * size
        searchSourceBuilder.from((searchParamVo.getPageNumber() - 1) * EsConstant.PRODUCT_PAGE_SIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);

        //高亮
        if (!StringUtils.isEmpty(searchParamVo.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'");
            highlightBuilder.postTags("b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }
        /**
         * 聚合分析
         */
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg");
        brandAgg.field("brandId").size(50);
        //品牌的子聚合
        brandAgg.subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brandImgAgg").field("brandImg").size(1));
        searchSourceBuilder.aggregation(brandAgg);


        //分类聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalogAgg").field("catalogId").size(20);;
        //分类聚合的子聚合
        catalogAgg.subAggregation(AggregationBuilders.terms("catalogName").field("catalogName").size(1));
        searchSourceBuilder.aggregation(catalogAgg);

        //属性聚合
        NestedAggregationBuilder attrNestedAgg = AggregationBuilders.nested("attrAgg", "attrs");
        NestedAggregationBuilder attrIdAgg = attrNestedAgg.subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId"));
        //聚合分析当前attr对应的name
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName").size(1));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue").size(50));
        searchSourceBuilder.aggregation(attrIdAgg);

        System.out.println(searchSourceBuilder.toString());
        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
    }
}
