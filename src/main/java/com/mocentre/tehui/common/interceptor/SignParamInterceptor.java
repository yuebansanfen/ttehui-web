/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.interceptor;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.mocentre.common.PlainResult;
import com.mocentre.commons.SignUtil;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.utils.response.BaseResult;
import com.mocentre.tehui.frontend.model.MemberAccountFTInstance;
import com.mocentre.tehui.frontend.service.MemberAccountManageService;

/**
 * 类SignParamInterceptor.java的实现描述：签名参数拦截器
 * 
 * @author sz.gong 2017年6月30日 下午3:01:16
 */
public class SignParamInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private MemberAccountManageService mebAcctManageService;
    @Autowired
    private RedisCache                 redisCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String reqUrl = request.getRequestURI();
        Map<String, String> params = new HashMap<String, String>();
        Enumeration enumeration = request.getParameterNames();
        String sign = "";
        String appKey = "";
        while (enumeration.hasMoreElements()) {
            String propertyName = (String) enumeration.nextElement();
            String propertyValue = request.getParameter(propertyName).trim();
            propertyValue = propertyValue.replace("\'", "\"");
            params.put(propertyName, propertyValue);
            if ("sign".equals(propertyName)) {
                sign = propertyValue;
            } else if ("appKey".equals(propertyName)) {
                appKey = propertyValue;
            }
        }
        boolean isAjax = CommUtil.isAjax(request);
        boolean isSiUrl = this.isSimulatedReq(reqUrl);
        String base = request.getContextPath();
        //先取缓存
        String appSercet = redisCache.getMebAccountAppSecret(appKey);
        if (StringUtils.isBlank(appSercet)) {
            PlainResult<MemberAccountFTInstance> pr = mebAcctManageService.getMemberAccountByAppKey(appKey,
                    CommUtil.generateUUID());
            if (!pr.isSuccess()) {
                if (isAjax || isSiUrl) {
                    BaseResult br = new BaseResult();
                    br.setErrorMessage("501", "无效的appKey");
                    this.writeMessageUtf8(response, br.toJsonString());
                } else {
                    this.writeMessageUtf8(response, "<script type='text/javascript'>window.location.href='" + base
                            + "/front/thirdOrder/error.htm?errorCode=501';</script>");
                }
                return false;
            }
            MemberAccountFTInstance mebAutIns = pr.getData();
            appSercet = mebAutIns.getAppSecret();
            redisCache.saveMebAccountAppSecret(appKey, appSercet);
        }
        if (StringUtils.isBlank(appSercet)) {
            if (isAjax || isSiUrl) {
                BaseResult br = new BaseResult();
                br.setErrorMessage("501", "无效的appKey");
                this.writeMessageUtf8(response, br.toJsonString());
            } else {
                this.writeMessageUtf8(response, "<script type='text/javascript'>window.location.href='" + base
                        + "/front/thirdOrder/error.htm?errorCode=501';</script>");
            }
            return false;
        }
        String mySign = SignUtil.buildMysign(appSercet, params);
        if (!mySign.equals(sign)) {
            if (isAjax || isSiUrl) {
                BaseResult br = new BaseResult();
                br.setErrorMessage("500", "签名失败");
                this.writeMessageUtf8(response, br.toJsonString());
            } else {
                this.writeMessageUtf8(response, "<script type='text/javascript'>window.location.href='" + base
                        + "/front/thirdOrder/error.htm?errorCode=500';</script>");
            }
            return false;
        }
        return true;
    }

    private void writeMessageUtf8(HttpServletResponse response, String result) {
        PrintWriter out = null;
        try {
            //response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            out = response.getWriter();
            out.write(result);
        } catch (Exception e) {

        } finally {
            if (out != null) {
                out.print("");
                out.close();
            }
        }
    }

    private boolean isSimulatedReq(String reqUrl) {
        if (reqUrl.indexOf("payment") > 0) {
            return true;
        }
        return false;
    }

}
