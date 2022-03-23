package com.wch.gulimall.thirdparty.service.iml;

import com.wch.gulimall.thirdparty.service.SmsService;
import com.wch.gulimall.thirdparty.utils.SmsUtils;
import org.springframework.stereotype.Service;


/**
 * @author wch
 * @version 1.0
 * @date 2022/3/22 18:52
 */
@Service
public class SmsServiceImpl implements SmsService {



    /**
     * 发送短信方法
     */
    @Override
    public boolean send(String phone, String code) {
        String time = "5";
        //发送验证码
        boolean res = SmsUtils.SendCode("+86" + phone, code, time);
        if (res){
            return true;
        }else {
            throw new RuntimeException("验证码发送失败");
        }
    }
}
