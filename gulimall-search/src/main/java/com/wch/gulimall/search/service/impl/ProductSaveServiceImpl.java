package com.wch.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.wch.common.constant.SearchConstant;
import com.wch.common.to.SkuEsModel;
import com.wch.gulimall.search.config.ElasticSearchConfig;
import com.wch.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/9 22:22
 */
@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    /**
     * 上架商品
     * @param skuEsModels
     */
    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //保存到ES
        //1， 在es中建立一个索引, 建立映射关系
        //2， 给es中保存数据
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuESModel : skuEsModels) {
            bulkRequest.add(
                    new IndexRequest(SearchConstant.ESIndex.ES_PRODUCT_INDEX)
                            .id(skuESModel.getSkuId().toString())
                            .source(JSON.toJSONString(skuESModel), XContentType.JSON)
            );
        }
        bulkRequest.timeout(TimeValue.timeValueMinutes(2));
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        if (bulk.hasFailures()) {
            List<String> strings = Arrays.stream(bulk.getItems()).map(item -> item.getId() + ", " + item.getFailure() + ", " + item.getFailureMessage() + "\n").collect(Collectors.toList());
            log.error("商品sku保存至ES失败: {}", strings);
            return false;
        }
        return true;
    }

}
