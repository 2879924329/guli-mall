package com.wch.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.wch.gulimall.search.config.ElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.InternalOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
class GulimallSearchApplicationTests {


    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Test
    void contextLoads() {
        System.out.println(restHighLevelClient);

    }

    @Test
    void searchData() throws IOException {
        //创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        //指定索引
        searchRequest.indices("bank");
        //指定dsl， 检索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建检索条件
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
   /*     searchSourceBuilder.from();
        searchSourceBuilder.size();*/
        //按照年龄的值分布进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);
        //计算平均薪资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAvg);
        System.out.println("检索条件：" + searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);
        //执行检索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
        //分析结果
        System.out.println(searchResponse.toString());
  /*      Map map = JSON.parseObject(searchResponse.toString(), Map.class);*/

        //获取所有查到的数据
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits){
            /*searchHit.getIndex();
            searchHit.getType();
            searchHit.getId();*/
            String sourceAsString = searchHit.getSourceAsString();
            //其他操作，将json转为bean

        }
        //获取这次检索到的分析信息
        Aggregations aggregations = searchResponse.getAggregations();
  /*      for (Aggregation aggregation : aggregations.asList()){
            System.out.println("当前聚合：" + aggregation.getName());

        }*/
        Terms ageAgg1 = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg1.getBuckets()){
            String keyAsString = bucket.getKeyAsString();
            System.out.println(keyAsString);
        }
        Avg balanceAvg1 = aggregations.get("balanceAvg");
        System.out.println("平均薪资：" + balanceAvg1.getValue());

    }

    @Test
    void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        /*indexRequest.source("userName", "lisi", "age" , 19, "gender", "男");*/
        User user = new User();
        user.setUsername("lisi");
        user.setAge(23);
        user.setGender("M");
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);
        //执行操作
        IndexResponse index = restHighLevelClient.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);
        //提取响应数据
        System.out.println(index);

    }
    @Data
    class User{
        private String username;
        private Integer age;
        private String gender;
    }

}
