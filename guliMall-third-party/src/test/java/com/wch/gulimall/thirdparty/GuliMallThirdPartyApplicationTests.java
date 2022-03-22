package com.wch.gulimall.thirdparty;


import com.wch.gulimall.thirdparty.component.SmsComponent;
import com.wch.gulimall.thirdparty.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class GuliMallThirdPartyApplicationTests {

    @Autowired
    private SmsComponent smsComponent;

    @Test
    void smsTest(){
        smsComponent.sendCode("14787025700", "123414");
    }

    @Test
    void contextLoads() {
            String host = "https://jumsendsms.market.alicloudapi.com";
            String path = "/sms/send-upgrade";
            String method = "POST";
            String appcode = "f01b7961ffe44a368a07e593d1711561";
            Map<String, String> headers = new HashMap<String, String>();
            //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
            headers.put("Authorization", "APPCODE " + appcode);
            Map<String, String> querys = new HashMap<String, String>();
            querys.put("mobile", "14787025700");
            querys.put("templateId", "M72CB42894");
            querys.put("value", "123456");
            Map<String, String> bodys = new HashMap<String, String>();

            try {
                /**
                 * 重要提示如下:
                 * HttpUtils请从
                 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
                 * 下载
                 *
                 * 相应的依赖请参照
                 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
                 */
                HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
                System.out.println(response.toString());
                //获取response的body
                //System.out.println(EntityUtils.toString(response.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Test
        void smsTests(){
            String host = "https://jumsendv2.market.alicloudapi.com";
            String path = "/sms/send-v2-status";
            String method = "GET";
            String appcode = "f01b7961ffe44a368a07e593d1711561";
            Map<String, String> headers = new HashMap<String, String>();
            //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
            headers.put("Authorization", "APPCODE " + appcode);
            Map<String, String> querys = new HashMap<String, String>();
            querys.put("mobile", "mobile");
            querys.put("taskId", "taskId");


            try {
                /**
                 * 重要提示如下:
                 * HttpUtils请从
                 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
                 * 下载
                 *
                 * 相应的依赖请参照
                 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
                 */
                HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
                System.out.println(response.toString());
                //获取response的body
                //System.out.println(EntityUtils.toString(response.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Test
        void setTest(){
            String host = "https://intlsms.market.alicloudapi.com";
            String path = "/comms/sms/sendmsgall";
            String method = "POST";
            String appcode = "f01b7961ffe44a368a07e593d1711561";
            Map<String, String> headers = new HashMap<String, String>();
            //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
            headers.put("Authorization", "APPCODE " + appcode);
            //根据API的要求，定义相对应的Content-Type
            headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            Map<String, String> querys = new HashMap<String, String>();
            Map<String, String> bodys = new HashMap<String, String>();
            bodys.put("callbackUrl", "http://test.dev.esandcloud.com");
            bodys.put("channel", "0");
            bodys.put("mobile", "14787025700");
            bodys.put("templateID", "M72CB42894");
            bodys.put("templateParamSet", "1234, 1");


            try {
                /**
                 * 重要提示如下:
                 * HttpUtils请从
                 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
                 * 下载
                 *
                 * 相应的依赖请参照
                 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
                 */
                HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
                System.out.println(response.toString());
                //获取response的body
                //System.out.println(EntityUtils.toString(response.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


