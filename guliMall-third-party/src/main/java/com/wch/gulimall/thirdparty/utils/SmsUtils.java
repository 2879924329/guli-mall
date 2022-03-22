package com.wch.gulimall.thirdparty.utils;


import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/22 18:50
 */
public class SmsUtils {
    private static final String SECRET_ID = "AKIDpUu3vb08zauxkHZZIkwrVszoMddNr41i";
    private static final String SECRET_KEY = "RbU7TbgbGelbBUWHn1ye3B2btvf8KGvJ";
    public static boolean SendCode(String phoneNumber,String code, String time) {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(SECRET_ID, SECRET_KEY);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            /* SDK默认用TC3-HMAC-SHA256进行签名
             * 非必要请不要修改这个字段 */
            clientProfile.setSignMethod("HmacSHA256");
            /* 实例化要请求产品(以sms为例)的client对象
             * 第二个参数是地域信息，可以直接填写字符串ap-guangzhou，或者引用预设的常量 */
            SmsClient client = new SmsClient(cred, "ap-guangzhou",clientProfile);

            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet = {phoneNumber};
            req.setPhoneNumberSet(phoneNumberSet);

            req.setSmsSdkAppId("1400619818");
            req.setSignName("wuCoding公众号");
            req.setTemplateId("1270920");

            String[] templateParamSet = {code,time};
            req.setTemplateParamSet(templateParamSet);

            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);

            // 输出json格式的字符串回包
            System.out.println(SendSmsResponse.toJsonString(resp));

            //获取发送状态，若成功则返回true
            for (SendStatus sendStatus : resp.getSendStatusSet()) {
                if ("send success".equals(sendStatus.getMessage()) &&
                        "Ok".equals(sendStatus.getCode())){
                    return true;
                }
            }
        } catch (TencentCloudSDKException e) {
            e.printStackTrace();
        }
        //若不成，返回false
        return false;
    }
}
