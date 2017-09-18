/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.upyun;

import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.mocentre.tehui.core.utils.DateUtils;

/**
 * 类YunFormUploader.java的实现描述：又拍云上传
 * 
 * @author sz.gong 2017年1月10日 上午10:24:03
 */
public class YunFormUploader {

    //默认过期时间
    private int    expiration = 1800;

    //默认域名
    private String apiDomain  = "https://v0.api.upyun.com";

    //空间名
    private String bucketName;

    //表单密匙
    private String apiKey;

    public YunFormUploader(String bucketName, String apiKey) {
        this.bucketName = bucketName;
        this.apiKey = apiKey;
    }

    public String getPolicy(Map<String, Object> paramsMap) {
        paramsMap.put(YunParams.BUCKET, bucketName);
        if (paramsMap.get(YunParams.EXPIRATION) == null) {
            paramsMap.put(YunParams.EXPIRATION, DateUtils.getSeconds(new Date()) + expiration);
        }
        //paramsMap.put("save-key", "/uploads/{year}/{mon}/{day}/{random32}{.suffix}");
        if (paramsMap.get(YunParams.ALLOW_FILE_TYPE) == null) {
            paramsMap.put(YunParams.ALLOW_FILE_TYPE, "gif,jpeg,jpg,png");
        }
        if (paramsMap.get(YunParams.CONTENT_LENGTH_RANGE) == null) {
            paramsMap.put("content-length-range", "0,1024000");
        }
        return Base64.encodeBase64String(JSON.toJSONString(paramsMap).getBytes());
    }

    public String getSignature(String policy) {
        return DigestUtils.md5Hex(policy + "&" + this.apiKey);
    }

    public String getAction() {
        return this.apiDomain + "/" + this.bucketName;
    }

    public int getExpiration() {
        return expiration;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    /**
     * 查看当前的域名接入点
     *
     * @return
     */
    public String getApiDomain() {
        return apiDomain;
    }

    /**
     * 切换 API 接口的域名接入点
     * <p>
     * 可选参数：<br>
     * 1) UpYun.ED_AUTO(v0.api.upyun.com)：默认，根据网络条件自动选择接入点 <br>
     * 2) UpYun.ED_TELECOM(v1.api.upyun.com)：电信接入点<br>
     * 3) UpYun.ED_CNC(v2.api.upyun.com)：联通网通接入点<br>
     * 4) UpYun.ED_CTT(v3.api.upyun.com)：移动铁通接入点
     *
     * @param domain 域名接入点
     */
    public void setApiDomain(String apiDomain) {
        this.apiDomain = apiDomain;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

}
