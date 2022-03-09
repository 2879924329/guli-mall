package com.wch.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.wch.gulimall.search.config.ElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

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
    void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        /*indexRequest.source("userName", "lisi", "age" , 19, "gender", "男");*/
        User user = new User();
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString);
        //执行操作
        IndexResponse index = restHighLevelClient.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);


    }
    @Data
    class User{
        private String username;
        private Integer age;
        private String gender;
    }

}
