/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.front.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mocentre.commons.httpclient.HttpProtocolHandler;
import com.mocentre.commons.httpclient.HttpRequest;
import com.mocentre.commons.httpclient.HttpResponse;
import com.mocentre.commons.httpclient.HttpResultType;
import com.mocentre.tehui.common.SystemConfig;
import com.mocentre.tehui.common.constant.ConfigConstant;
import com.mocentre.tehui.common.constant.Constants;
import com.mocentre.tehui.common.util.CookieUtil;
import com.mocentre.tehui.common.util.LoggerUtil;
import com.mocentre.tehui.core.controller.BaseController;

/**
 * 类WxOauthController.java的实现描述：微信授权回调
 * 
 * @author sz.gong 2017年8月1日 下午1:46:34
 */
@Controller
@RequestMapping("/front/wxOauth")
public class WxOauthController extends BaseController {

    /**
     * 商城微信支付获取openid回调
     * 
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "access.htm", method = RequestMethod.GET)
    public String access(HttpServletRequest request, HttpServletResponse response, Model model) {
        //String paymentNum = request.getParameter("paymentNum");
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        SystemConfig config = SystemConfig.INSTANCE;
        String appid = config.getValue(ConfigConstant.WX_APPID);
        String appsecret = config.getValue(ConfigConstant.WX_APPSECRET);
        String tokenUrl = config.getValue(ConfigConstant.WX_OAUTH2_TOKEN_URL);
        String cashUrl = config.getValue(ConfigConstant.WX_CASH_URL);
        try {
            if (StringUtils.isBlank(code)) {
                model.addAttribute("errorMsg", "参数错误");
                return getErrorWap();
            }
            HttpProtocolHandler handler = HttpProtocolHandler.getInstance();
            HttpRequest req = new HttpRequest(HttpResultType.STRING);
            req.setUrl(tokenUrl);
            req.setMethod(HttpRequest.METHOD_GET);
            req.setCharset("utf-8");
            req.setQueryString("appid=" + appid + "&secret=" + appsecret + "&code=" + code
                    + "&grant_type=authorization_code");
            HttpResponse res = handler.execute(req);
            String resStr = res.getStringResult();
            if (StringUtils.isNotBlank(resStr)) {
                JSONObject jObj = JSON.parseObject(resStr);
                String openid = jObj.getString("openid");
                CookieUtil.setCookie(response, Constants.COOKIE_USER_OPENID, openid, 24 * 60 * 60);
                LoggerUtil.tehuiwebLog.info("openid=" + openid);
            }
        } catch (Exception e) {
            LOGGER.error("access", e);
        }
        cashUrl = cashUrl + "?paymentNum=" + state;
        return "redirect:" + cashUrl;
    }

    /**
     * 第三方使用微信支付获取openid回调
     * 
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "td_access.htm", method = RequestMethod.GET)
    public String tdAccess(HttpServletRequest request, HttpServletResponse response, Model model) {
        String code = request.getParameter("code");
        String appKey = request.getParameter("appKey");
        String orderNum = request.getParameter("orderNum");
        String state = request.getParameter("state");
        SystemConfig config = SystemConfig.INSTANCE;
        String appid = config.getValue(ConfigConstant.WX_APPID);
        String appsecret = config.getValue(ConfigConstant.WX_APPSECRET);
        String tokenUrl = config.getValue(ConfigConstant.WX_OAUTH2_TOKEN_URL);
        try {
            if (StringUtils.isBlank(code)) {
                model.addAttribute("errorMsg", "参数错误");
                return getErrorWap();
            }
            HttpProtocolHandler handler = HttpProtocolHandler.getInstance();
            HttpRequest req = new HttpRequest(HttpResultType.STRING);
            req.setUrl(tokenUrl);
            req.setMethod(HttpRequest.METHOD_GET);
            req.setCharset("utf-8");
            req.setQueryString("appid=" + appid + "&secret=" + appsecret + "&code=" + code
                    + "&grant_type=authorization_code");
            HttpResponse res = handler.execute(req);
            String resStr = res.getStringResult();
            if (StringUtils.isNotBlank(resStr)) {
                JSONObject jObj = JSON.parseObject(resStr);
                String openid = jObj.getString("openid");
                CookieUtil.setCookie(response, Constants.COOKIE_USER_OPENID, openid, 24 * 60 * 60);
                LoggerUtil.tehuiwebLog.info("openid=" + openid);
            }
        } catch (Exception e) {
            LOGGER.error("tdAccess", e);
        }
        String cxtPath = request.getContextPath();
        String path = "https://" + request.getServerName() + cxtPath;
        String mySign = DigestUtils.md5Hex("appKey=" + appKey + "orderNum=" + orderNum);
        return "redirect:" + path + "/front/thirdOrder/wxDetail.htm?orderNum=" + orderNum + "&appKey=" + appKey
                + "&sign=" + mySign;
    }

}
