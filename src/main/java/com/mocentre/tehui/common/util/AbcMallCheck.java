/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.util;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 类AbcParamHandler.java的实现描述：农行客户端验证
 * 
 * @author sz.gong 2017年6月5日 下午1:38:30
 */
public class AbcMallCheck {

    /** 密钥 */
    private String    appSecret;

    /** 请求的参数 */
    private SortedMap parameters;

    /** debug信息 */
    private String    debugInfo;

    /**
     * 构造函数
     */
    public AbcMallCheck(String appSecret, String reqParamters) {
        this.appSecret = appSecret;
        this.parameters = new TreeMap();
        this.debugInfo = "";
        String[] paramArray = reqParamters.split("&");
        for (int i = 0; i < paramArray.length; i++) {
            String[] param = paramArray[i].split("=");
            if (param.length == 2) {
                parameters.put(param[0].trim(), param[1]);
            }
        }
    }

    /**
     * 构造函数
     */
    public AbcMallCheck(String appSecret,SortedMap parameters) {
        this.appSecret = appSecret;
        this.parameters = parameters;
        this.debugInfo = "";
    }

    /**
     * 设置参数值
     * 
     * @param parameter 参数名称
     * @param parameterValue 参数值
     */
    public void setParameter(String parameter, String parameterValue) {
        String v = "";
        if (null != parameterValue) {
            v = parameterValue.trim();
        }
        this.parameters.put(parameter.toLowerCase(), v);
    }

    /**
     * 返回所有的参数
     * 
     * @return SortedMap
     */
    public SortedMap getAllParameters() {
        return this.parameters;
    }

    public String getSha256Sign() {
        StringBuffer sb = new StringBuffer();
        this.parameters.put("appsecret", this.getAppSecret());
        Set es = this.parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k)) {
                sb.append(k.toLowerCase() + "=" + v + "&");
            }
        }
        sb.deleteCharAt(sb.length() - 1);

        String sign = getSHA256Encrypt(sb.toString());

        //debug信息
        this.setDebugInfo(sb.toString() + " => sign:" + sign);
        LoggerUtil.tehuiwebLog.info(sb.toString() + " => sign:" + sign);
        return sign;

    }

    private String getSHA256Encrypt(String orignal) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != md) {
            byte[] origBytes = orignal.getBytes();
            md.update(origBytes);
            byte[] digestRes = md.digest();
            String digestStr = getDigestStr(digestRes);
            return digestStr;
        }
        return null;
    }

    private String getDigestStr(byte[] origBytes) {
        String tempStr = null;
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < origBytes.length; i++) {
            // 这里按位与是为了把字节转整时候取其正确的整数，Java中一个int是4个字节
            // 如果origBytes[i]最高位为1，则转为int时，int的前三个字节都被1填充了
            tempStr = Integer.toHexString(origBytes[i] & 0xff);
            if (tempStr.length() == 1) {
                stb.append("0");
            }
            stb.append(tempStr);
        }
        return stb.toString();
    }

    public String getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(String debugInfo) {
        this.debugInfo = debugInfo;
    }

    public String getAppSecret() {
        return appSecret;
    }

}
