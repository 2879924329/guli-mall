package com.wch.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.wch.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000119659403";
    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDKY1T5fkZomY2obogQKHRaB7cxd/6k9pL+gwLnvBRxw1t9RfIjOmZ0e5ocucTEUPZlGTCYNKb2TH2jl7GmnsYx1X8Sw++r7ObLputgHB03VCn4gca/wiwmu2Xj9qgbZdWbM3CfcZGKOQ7pEsMPY+Kxt3iZFLBRYYO5kC/TyMchdCBh/F2srKYryE4advgFh144nVLl9RZ1x8NHaOSAFMeMDws/62AtQT9+48IyZ+3xiGQDr0uCP4hGqeOcWFiiJGoZluF80tDYW0oD1NFSpJ+7o8B0EQiHabG9yVoE6w06DmwUla9NDjWCHkxT9bsGYUahIW+UPJcAtVPh372PdA6LAgMBAAECggEADmJ0/a6d7n1Ldjc08IEPk/6MZLW0oaDs3a4OHM5fXZ6W+BZqmVpjQdAvKe+CLrnaaWEa9RZ473SMUX5MoB0U1SHpgDnw5PVXig35GMGmyfHvcbw3lhWE8dPL8CHt3NSmE/vZ+1slDv/zMVmmB4f/6sWxip/P0Fjv9dZrr8/LZ3MrJdVxegBcDhiIlYxLyTMed3+4C6bEGt2DaRe7RrsU4CSKa6mRNDxYeUdC3uokpPG5XWnzrLsrBeyRX/taK0jP1/hSlG+mJ0B1kaI1XHkA0u+0Z3R7BWKiSimEbAnu7dL3qd3F35l6LlnTAEnWsUqYrE3mxGkblCf804W8d/9PIQKBgQDnE8s9iTlG0/cOguy5cMctvfxtp9fiaz5mRqQWz1BJHtTtZNPYviisitzww45pKuo9KRixuLGbX6AOTkLDM3yxpVKBvUh56SWhWhMzlum2nkTYb71N5zDtj0jGe2wOKQYTr7qGSPt1LhcccdBPjwzprCedSBrXGuXYzS8y42KcMwKBgQDgN2gDQq0vuSw5NlLh1m6l8qYyVH4qKzQNKz6So47D8A/ggpu2mYyb4JXsdpkhdbKUYUahVYtNBWX7vwpdHniA5wNFp+U1sIW+iCIuBM5Lfrq5dSonV7HN6661vwG+/1dnoHZ9P3cCSepQab1F6/34HLwZLXiyFqNZeQOj99BsSQKBgHOjnzkrXAOK/f9AG/Ftc+/7PMEvlsUTgEW0oldb5kTBbjNjGvVDrJ00XP8MDk+pD1IOJ/KAiDlGMJYYKib/tC4+mrycmhGQUKmfpy5RHqKqL6/Us3qx8PVhmDzj9R5c/1KIxaKAEX8B0sUEXAgEgaTK54Q5Wl09c7r8a9tembGrAoGAThz/b6NbErVAYu46EdPS2J2gqQgmrCpBCBV4xz2UD08jOORHu2XjRqsHkEoBL1cB0A/Tu4FB9qNa1EnKIobrFH1m9m13vSyya56hycaC2GpeQx4+gVZb65GgXFEfGpt6p++PWDM0EugNadZsyl9dK4NXRK5Czkh0wEcYrZc4VYECgYB1yIrXX7sQOodM3HJJeE9P7wvt5pYjaJ13wyNcIbGK+8fkiNp/y2pijPX4c2KGA5vlcAgixOwlRjcJ1cXP93ywT1cHvr9FxcJolW63mbLfY0mp5yP2xaHN0BVeCAwAEpa4fSvJ0JdmHS31ZpTuCu3tukLP8kTdLPzExhDFBZqJQQ==";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAymNU+X5GaJmNqG6IECh0Wge3MXf+pPaS/oMC57wUccNbfUXyIzpmdHuaHLnExFD2ZRkwmDSm9kx9o5expp7GMdV/EsPvq+zmy6brYBwdN1Qp+IHGv8IsJrtl4/aoG2XVmzNwn3GRijkO6RLDD2Pisbd4mRSwUWGDuZAv08jHIXQgYfxdrKymK8hOGnb4BYdeOJ1S5fUWdcfDR2jkgBTHjA8LP+tgLUE/fuPCMmft8YhkA69Lgj+IRqnjnFhYoiRqGZbhfNLQ2FtKA9TRUqSfu6PAdBEIh2mxvclaBOsNOg5sFJWvTQ41gh5MU/W7BmFGoSFvlDyXALVT4d+9j3QOiwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url  ;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url;

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
