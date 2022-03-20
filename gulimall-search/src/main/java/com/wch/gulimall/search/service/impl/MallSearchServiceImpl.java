package com.wch.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wch.common.constant.SearchConstant;
import com.wch.common.to.SkuEsModel;
import com.wch.common.utils.R;
import com.wch.gulimall.search.config.ElasticSearchConfig;
import com.wch.gulimall.search.constant.EsConstant;
import com.wch.gulimall.search.feign.ProductFeignService;
import com.wch.gulimall.search.service.MallSearchService;
import com.wch.gulimall.search.vo.AttrResponseVo;
import com.wch.gulimall.search.vo.BrandVo;
import com.wch.gulimall.search.vo.SearchParamVo;
import com.wch.gulimall.search.vo.SearchResultResponse;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/15 21:15
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ProductFeignService productFeignService;

    /**
     * 商品前台检索，自动将页面传过来的请求参数封装成对象
     *
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
            searchResultResponse = buildSearchResult(searchResponse, searchParamVo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResultResponse;
    }


    /**
     * 构建请求条件
     *
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParamVo param) {
        SearchRequest searchRequest = new SearchRequest();
        // 指定索引
        searchRequest.indices(SearchConstant.ESIndex.ES_PRODUCT_INDEX);
        // 构建搜索条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        // 构建bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 1.模糊匹配keyword
        if (!StringUtils.isEmpty(param.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        // 2.过滤(分类id。品牌id，价格区间，是否有库存，规格属性)，
        // 2.1 分类id
        if (param.getCatelog3Id() != null &&  param.getCatelog3Id() > 0) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catelogId", param.getCatelog3Id()));
        }
        // 2.2 品牌id
        List<Long> brandId = param.getBrandId();
        if (!CollectionUtils.isEmpty(brandId)) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", brandId));
        }
        // 2.3 价格区间 1_500 / _500 / 500_
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
        String price = param.getSkuPrice();
        if (!StringUtils.isEmpty(price)) {
            String[] priceInfo = price.split("_");
            // 1_500
            if (priceInfo.length == 2) {
                rangeQueryBuilder.gte(priceInfo[0]).lte(priceInfo[1]);
                //    _500
            } else if (price.startsWith("_")) {
                rangeQueryBuilder.lte(priceInfo[0]);
                //    500_
            } else {
                rangeQueryBuilder.gte(priceInfo[0]);
            }
        }
        boolQueryBuilder.filter(rangeQueryBuilder);
        // 2.4 库存
        if (param.getHasStock() != null) {
            boolean flag = param.getHasStock() != 0;
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", flag));
        }
        // 2.5 规格属性
        // attrs=1_钢精:铝合&attrs=2_anzhuo:apple&attrs=3_lisi ==> attrs=[1_钢精:铝合,2_anzhuo:apple,3_lisi]
        List<String> attrs = param.getAttrs();
        if (!CollectionUtils.isEmpty(attrs)) {
            // 每个属性参数 attrs=1_钢精:铝合 ==》 nestedQueryFilter
            /**
             *          {
             *           "nested": {
             *             "path": "",
             *             "query": {
             *               "bool": {
             *                 "must": [
             *                   {},
             *                   {}
             *                 ]
             *               }
             *             }
             *           }
             *         },
             */
            for (String attr : attrs) {
                String[] attrInfo = attr.split("_");
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                boolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrInfo[0]));
                boolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrInfo[1].split(":")));
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }

        // 第一部分bool查询组合结束
        builder.query(boolQueryBuilder);

        // 3.排序，sort=hotScore_asc/desc
        String sortStr = param.getSort();
        if (!StringUtils.isEmpty(sortStr)) {
            String[] sortInfo = sortStr.split("_");
            SortOrder sortType = sortInfo[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            builder.sort(sortInfo[0], sortType);
        }

        // 4.分页，
        builder.from(param.getPageNum() == null ? 0 : (param.getPageNum() - 1) * EsConstant.PRODUCT_PAGE_SIZE);
        builder.size(EsConstant.PRODUCT_PAGE_SIZE);
        // 5.高亮，查询关键字不为空才有结果高亮
        if (!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle").preTags("<b style='color:red'>").postTags("</b>");
            builder.highlighter(highlightBuilder);
        }
        // 6.聚合分析，分析得到的商品所涉及到的分类、品牌、规格参数，
        // term值的是分布情况，就是存在哪些值，每种值下有几个数据; size是取所有结果的前几种，(按id聚合后肯定是同一种，所以可以指定为1)
        // 6.1 分类部分，按照分类id聚合，划分出分类后，每个分类内按照分类名字聚合就得到分类名，不用再根据id再去查询数据库
        TermsAggregationBuilder catelogAgg = AggregationBuilders.terms("catalogAgg").field("catelogId");
        catelogAgg.subAggregation(AggregationBuilders.terms("catalogNameAgg").field("catelogName").size(1));
        builder.aggregation(catelogAgg);
        // 6.2 分类部分，按照品牌id聚合，划分出品牌后，每个品牌内按照品牌名字聚合就得到品牌名，不用再根据id再去查询数据库
        // 每个品牌内按照品牌logo聚合就得到品牌logo，不用再根据id再去查询数据库
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brandAgg").field("brandId");
        brandAgg.subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brandImgAgg").field("brandImg").size(1));
        builder.aggregation(brandAgg);
        // 6.3 规格参数部分，按照规格参数id聚合，划分出规格参数后，每个品牌内按照规格参数名字聚合就得到规格参数名，不用再根据id再去查询数据库
        // 每个规格参数内按照规格参数值聚合就得到规格参数值，不用再根据id再去查询数据库
        NestedAggregationBuilder nestedAggregationBuilder = AggregationBuilders.nested("attrAgg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId");
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName").size(1));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"));
        nestedAggregationBuilder.subAggregation(attrIdAgg);
        builder.aggregation(nestedAggregationBuilder);

        // 组和完成
        System.out.println("搜索参数构建的DSL语句：" + builder);
        searchRequest.source(builder);
        return searchRequest;
    }

    /**
     * 构建结果数据
     *
     * @param searchResponse
     * @return
     */
    private SearchResultResponse buildSearchResult(SearchResponse searchResponse, SearchParamVo param) {
        SearchResultResponse result = new SearchResultResponse();
        SearchHits hits = searchResponse.getHits();
        /**
         * 全部商品数据
         */
        List<SkuEsModel> esModels = Arrays.stream(hits.getHits()).map(hit -> {
            // 每个命中的记录的_source部分是真正的数据的json字符串
            String sourceAsString = hit.getSourceAsString();
            SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
            if (!StringUtils.isEmpty(param.getKeyword())) {
                String skuTitle = hit.getHighlightFields().get("skuTitle").getFragments()[0].toString();
                esModel.setSkuTitle(skuTitle);
            }
            return esModel;
        }).collect(Collectors.toList());
        result.setProducts(esModels);
        /**
         * 聚合结果--分类
         */
        Aggregations aggregations = searchResponse.getAggregations();
        // debug模式下确定这个返回的具体类型
        ParsedLongTerms catalogAgg = aggregations.get("catalogAgg");
        ArrayList<SearchResultResponse.CatalogVo> catalogVos = new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catalogAgg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResultResponse.CatalogVo catalogVo = new SearchResultResponse.CatalogVo();
            //分类id
            String catalogId = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(catalogId));
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalogNameAgg");
            //分类名
            String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
            catalogVos.add(catalogVo);
        }
        result.setCatalogVos(catalogVos);
        /**
         * 聚合结果--品牌，与上面过程类似
         */
        ParsedLongTerms brandAgg = aggregations.get("brandAgg");
        List<SearchResultResponse.BrandVo> brands = brandAgg.getBuckets().stream().map(bucket -> {
            long brandId = bucket.getKeyAsNumber().longValue();
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brandNameAgg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brandImgAgg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
            SearchResultResponse.BrandVo brandVO = new SearchResultResponse.BrandVo();
            brandVO.setBrandId(brandId);
            brandVO.setBrandName(brandName);
            brandVO.setBrandImg(brandImg);
            return brandVO;
        }).collect(Collectors.toList());
        result.setBrandVos(brands);
        /**
         * 聚合结果--规格参数
         */
        ParsedNested attrAgg = aggregations.get("attrAgg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        List<SearchResultResponse.AttrVo> attrs = attrIdAgg.getBuckets().stream().map(bucket -> {
            long attrId = bucket.getKeyAsNumber().longValue();
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
            // 根据id分类后肯定是同一类，只可能有一种名字，所以直接取第一个bucket
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            // 根据id分类后肯定是同一类，但是可以有多个值，所以会有多个bucket，把所有值组合起来
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
            List<String> attrValue = attrValueAgg.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
            SearchResultResponse.AttrVo attrVO = new SearchResultResponse.AttrVo();
            attrVO.setAttrId(attrId);
            attrVO.setAttrName(attrName);
            attrVO.setAttrValues(attrValue);
            return attrVO;
        }).collect(Collectors.toList());
        result.setAttrVos(attrs);
        /**
         * 分页信息
         */
        // 总记录数
        result.setTotalCount(hits.getTotalHits().value);

        // 每页大小
        result.setPageSize(EsConstant.PRODUCT_PAGE_SIZE);
        // 总页数
        long totalPage = (result.getTotalCount() + EsConstant.PRODUCT_PAGE_SIZE - 1) / EsConstant.PRODUCT_PAGE_SIZE;
        result.setTotalPage(totalPage);
        // 当前页码
        int pageNum = param.getPageNum() == null ? 1 : param.getPageNum();
        result.setCurrPage(pageNum);
        ArrayList<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i < totalPage; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);
        //面包屑导航功能
        if (!CollectionUtils.isEmpty(param.getAttrs())){
            List<SearchResultResponse.NavVo> collect = param.getAttrs().stream().map(attr -> {
                //分析每一个attrs传过来的查询参数值
                SearchResultResponse.NavVo navVo = new SearchResultResponse.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R info = productFeignService.info(Long.parseLong(s[0]));
                result.getAttrIds().add(Long.parseLong(s[0]));
                if (info.getCode() == 0) {
                    AttrResponseVo data = info.getData("attr", new TypeReference<AttrResponseVo>() {
                    });
                    navVo.setNavName(data.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }
                //取消面包屑以后，跳转到哪个地方， 将请求地址的url查询条件剔除
                //拿到所有的查询条件
                String replace = replaceQueryString(param, attr, "attrs");
                navVo.setLink("http://search.guli-mall.com/list.html?" + replace);
                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(collect);
        }
        if (!CollectionUtils.isEmpty(param.getBrandId())){
            List<SearchResultResponse.NavVo> navs = result.getNavs();
            SearchResultResponse.NavVo navVo = new SearchResultResponse.NavVo();
            navVo.setNavName("品牌");
            R r = productFeignService.brandInfo(param.getBrandId());
            if (r.getCode() == 0){
                List<BrandVo> brand = r.getData("brand", new TypeReference<List<BrandVo>>() {
                });
                StringBuffer stringBuffer = new StringBuffer();
                String replace = "";
                for (BrandVo brandVo : brand) {
                    stringBuffer.append(brandVo.getBrandName() + ":");
                    replace = replaceQueryString(param, brandVo.getBrandId() + "", "brandId");
                }
                navVo.setNavValue(stringBuffer.toString());
                navVo.setLink("http://search.guli-mall.com/list.html?" + replace);
            }
            navs.add(navVo);
        }


        //TODO 分类导航面包屑
        return result;
    }

    private String replaceQueryString(SearchParamVo param, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return param.getQueryString().replace("&" + key +"=" + encode, "");
    }
}
