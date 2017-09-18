/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.wechat.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mocentre.commons.httpclient.HttpProtocolHandler;
import com.mocentre.commons.httpclient.HttpRequest;
import com.mocentre.commons.httpclient.HttpResponse;
import com.mocentre.commons.httpclient.HttpResultType;
import com.mocentre.tehui.common.SystemConfig;
import com.mocentre.tehui.common.constant.ConfigConstant;

/**
 * 类ToolUtil.java的实现描述：获取openid
 * 
 * @author sz.gong 2017年2月16日 上午10:55:58
 */
public class ToolUtil {

    public String getOpenid(String jsCode) {
        String openid = null;
        SystemConfig config = SystemConfig.INSTANCE;
        HttpProtocolHandler handler = HttpProtocolHandler.getInstance();
        HttpRequest req = new HttpRequest(HttpResultType.STRING);
        StringBuffer buf = new StringBuffer();
        buf.append("appid=" + config.getValue(ConfigConstant.APPID));
        buf.append("&secret=" + config.getValue(ConfigConstant.SECRET));
        buf.append("&js_code=" + jsCode);
        buf.append("&grant_type=authorization_code");
        req.setUrl("https://api.weixin.qq.com/sns/jscode2session");
        req.setMethod(HttpRequest.METHOD_GET);
        req.setQueryString(buf.toString());
        try {
            HttpResponse res = handler.execute(req);
            String strResult = res.getStringResult();
            JSONObject jsonObj = JSON.parseObject(strResult);
            if (strResult.indexOf("openid") > 0) {
                openid = jsonObj.getString("openid");
                String sessionKey = jsonObj.getString("session_key");
            }
        } catch (Exception e) {
            return null;
        }
        return openid;
    }

}
