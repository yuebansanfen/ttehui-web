package com.mocentre.tehui.common.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mocentre.tehui.common.SystemConfig;
import com.mocentre.tehui.common.constant.ConfigConstant;
import com.mocentre.util.ApiClient;
import com.mocentre.util.CommonUtil;

/**
 * 短信服务工具类. Created by yukaiji on 2017/6/21.
 */
public class SMSUtil {

    public static String sendVerificationCode(String telephone, String message, Boolean async, String reqeustId) {
        SystemConfig config = SystemConfig.INSTANCE;
        String smsJson = config.getValue(ConfigConstant.SMS_JSON_DATA);
        JSONObject smsObj = JSON.parseObject(smsJson);
        String apiUrl = smsObj.getString("apiUrl");
        String publicKey = smsObj.getString("publicKey");
        String secretKey = smsObj.getString("secretKey");
        String channel = smsObj.getString("channel");
        ApiClient apiClient = new ApiClient();
        apiClient.setChannel(channel);
        apiClient.setPublicKey(publicKey);
        apiClient.setSecretKey(secretKey);
        apiClient.setUrl(apiUrl);
        apiClient.setAction("sendSms");
        ArrayList<Object> list = new ArrayList<Object>();
        HashMap<String, Object> uploadContent = new HashMap<String, Object>();
        uploadContent.put("phone", telephone);
        uploadContent.put("content", message);
        uploadContent.put("requestId", reqeustId);
        if (async) {
            uploadContent.put("isAsync", 1);
        } else {
            uploadContent.put("isAsync", 0);
        }
        list.add(uploadContent);
        String sendData = JSON.toJSONString(list);
        String res = apiClient.sendData(CommonUtil.getUtf8EscapedString(sendData), 5000);
        return res;
    }

}
