/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.front.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.mocentre.common.PlainResult;
import com.mocentre.msite.service.WeChatHttpService;
import com.mocentre.msite.service.impl.WeChatHttpServiceImpl;
import com.mocentre.msite.vo.TicketVo;
import com.mocentre.msite.vo.WeChatConfigVo;
import com.mocentre.tehui.backend.model.MemberAccountKeyInstance;
import com.mocentre.tehui.common.SystemConfig;
import com.mocentre.tehui.common.constant.ConfigConstant;
import com.mocentre.tehui.common.constant.Constants;
import com.mocentre.tehui.common.util.AbcMallCheck;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.common.util.LoggerUtil;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.CustomerInfoFTInstance;
import com.mocentre.tehui.frontend.model.MemberAccountFTInstance;
import com.mocentre.tehui.frontend.param.AbcMallParam;
import com.mocentre.tehui.frontend.service.LoginManageService;
import com.mocentre.tehui.frontend.service.MemberAccountManageService;

/**
 * 类InitController.java的实现描述：前端初始化控制器
 *
 * @author sz.gong 2017年5月5日 下午6:30:02
 */
@Controller
@RequestMapping("/main")
public class IndexFTController extends BaseController {

    @Autowired
    private LoginManageService         loginManageService;
    @Autowired
    private RedisCache                 redisCache;
    @Autowired
    private MemberAccountManageService memberAccountManageService;

    @RequestMapping(value = "index")
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
        String baseUrl = SystemConfig.INSTANCE.getValue(ConfigConstant.VUE_SHOP_BASE_URL);
        String staticUrl = SystemConfig.INSTANCE.getValue(ConfigConstant.VUE_SHOP_STATIC_URL);
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("staticUrl", staticUrl);
        return getNameSpace() + "index";
    }

    @RequestMapping(value = "gift")
    public String gift(HttpServletRequest request, HttpServletResponse response, Model model) {
        String baseUrl = SystemConfig.INSTANCE.getValue(ConfigConstant.VUE_GIFT_BASE_URL);
        String staticUrl = SystemConfig.INSTANCE.getValue(ConfigConstant.VUE_GIFT_STATIC_URL);
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("staticUrl", staticUrl);
        return getNameSpace() + "gift";
    }

    /**
     * @param request
     * @param response
     * @param model 
     *            参数eg:id=f28b17c428460d1ca9491000be9e03f75cc3e1af3e96&cityCode
     *            =131& longitude
     *            =116&latitude=39&seqno=YH20160530124548123342&appid=123456
     *            &sign=8e07339f
     *            cc741c4df0acaeee7c6c12cc7ef0306d6650e187673532aa338f6a7e
     * @return
     */
    @RequestMapping(value = "malls")
    public String abcMalls(HttpServletRequest request, HttpServletResponse response, Model model) {

        String baseUrl = SystemConfig.INSTANCE.getValue(ConfigConstant.VUE_ABCSHOP_BASE_URL);
        String staticUrl = SystemConfig.INSTANCE.getValue(ConfigConstant.VUE_ABCSHOP_STATIC_URL);
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("staticUrl", staticUrl);
        CustomerInfoFTInstance customerInfo = redisCache.currentLoginUser(request);
        String aid = request.getParameter("id");
        boolean needCheck = customerInfo == null && StringUtils.isNotBlank(aid);
        if (customerInfo != null) {
            String abcaid = customerInfo.getAbcaid();
            if (StringUtils.isNotBlank(aid) && !aid.equals(abcaid)) {
                redisCache.clearLoginInfo(customerInfo);
                needCheck = true;
            }
        }
        if (needCheck) {
            String reqParams = request.getQueryString();
            LoggerUtil.tehuiwebLog.info("ABC reqParams:" + reqParams);
            if (StringUtils.isNotBlank(reqParams)) {
                String appSecret = "";
                AbcMallCheck mallCheck = new AbcMallCheck(appSecret, reqParams);
                String signed = mallCheck.getSha256Sign();
                Map paramsMap = mallCheck.getAllParameters();
                Object signObj = paramsMap.get("sign");
                Object idObj = paramsMap.get("id");
                Object citycodeObj = paramsMap.get("cityCode");
                Object longitudeObj = paramsMap.get("longitude");
                Object latitudeObj = paramsMap.get("latitude");
                String sign = "";
                if (signObj != null) {
                    sign = (String) signObj;
                }
                boolean isRight = StringUtils.equals(StringUtils.lowerCase(sign), StringUtils.lowerCase(signed));
                if (isRight && idObj != null) {
                    AbcMallParam mallParam = new AbcMallParam();
                    mallParam.setRequestId(generageRequestId());
                    mallParam.setId((String) idObj);
                    mallParam.setCityCode(citycodeObj != null ? (String) citycodeObj : null);
                    mallParam.setLongitude(longitudeObj != null ? (String) longitudeObj : null);
                    mallParam.setLatitude(latitudeObj != null ? (String) latitudeObj : null);
                    PlainResult<CustomerInfoFTInstance> pr = loginManageService.loginByAbcaid(mallParam);
                    if (pr.isSuccess()) {
                        CustomerInfoFTInstance cumInfo = pr.getData();
                        String uuidKey = redisCache.saveLoginUser(cumInfo);
                        Cookie cookie = new Cookie(Constants.COOKIE_TICKET, uuidKey);
                        cookie.setDomain(Constants.COOKIE_DOMAIN);
                        cookie.setPath("/");
                        cookie.setMaxAge(1 * 24 * 60 * 60);
                        response.addCookie(cookie);
                    }
                }
            }
        } else if (customerInfo != null) {
            redisCache.delayLoginUserTime(customerInfo);
        }
        return getNameSpace() + "mall";
    }

    @RequestMapping(value = "mall")
    public String abcMall(HttpServletRequest request, HttpServletResponse response, Model model) {
        String keymark = request.getParameter("keymark");
        if (StringUtils.isBlank(keymark)) {
            model.addAttribute("errorMsg", "参数错误");
            return getErrorWap();
        }
        MemberAccountKeyInstance mebAut = redisCache.getMebAccount(keymark);
        if (mebAut == null || StringUtils.isBlank(mebAut.getAppSecret())) {
            mebAut = new MemberAccountKeyInstance();
            PlainResult<MemberAccountFTInstance> pr = memberAccountManageService.getMemberAccountByKeymark(keymark,
                    generageRequestId());
            if (!pr.isSuccess()) {
                model.addAttribute("errorMsg", "活动不存在");
                return getErrorWap();
            }
            MemberAccountFTInstance mebIns = pr.getData();
            mebAut.setAbcAppsecret(mebIns.getAbcAppsecret());
            mebAut.setAppKey(mebIns.getAppKey());
            mebAut.setAppSecret(mebIns.getAppSecret());
            mebAut.setPageAddress(mebIns.getPageAddress());
            redisCache.saveMebAccount(keymark, mebAut);
        }
        String reqParams = request.getQueryString();
        String pageAddress = mebAut.getPageAddress();
        String redirectParams = reqParams + pageAddress;
        return "redirect:" + "/main/tomall?" + redirectParams;
    }

    @RequestMapping(value = "tomall")
    public String toMall(HttpServletRequest request, HttpServletResponse response, Model model) {

        String baseUrl = SystemConfig.INSTANCE.getValue(ConfigConstant.VUE_ABCSHOP_BASE_URL);
        String staticUrl = SystemConfig.INSTANCE.getValue(ConfigConstant.VUE_ABCSHOP_STATIC_URL);
        boolean isWx = CommUtil.isWeixin(request);
        model.addAttribute("isWx", isWx);
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("staticUrl", staticUrl);
        String citycode = "", longitude = "", latitude = "";
        String keymark = request.getParameter("keymark");
        if (StringUtils.isBlank(keymark)) {
            model.addAttribute("errorMsg", "参数错误");
            return getErrorWap();
        }
        CustomerInfoFTInstance customerInfo = redisCache.currentLoginUser(request);
        if (isWx) {
            String ctxPath = request.getContextPath();
            int sevPort = request.getServerPort();
            String sevName = request.getServerName();
            String fromUrl = "https://" + request.getServerName() + ctxPath + "/main/tomall?keymark=" + keymark;
            WeChatConfigVo chatConfig = new WeChatConfigVo();
            WeChatHttpService chatService = new WeChatHttpServiceImpl(
                    "https://sptest.mocentre.cn/main/tomall?keymark=missfresh#/activity/deliciousDaily");
            String ticket = redisCache.getWxJSticket();
            if (StringUtils.isBlank(ticket)) {
                TicketVo ticketVo = chatService.getTicketVo();
                if (ticketVo != null) {
                    ticket = ticketVo.getTicket();
                    redisCache.saveJSticket(ticket);
                }
            }
            if (StringUtils.isNotBlank(ticket)) {
                chatConfig = chatService.getWeChatConfig(ticket);
            }
            model.addAttribute("wxConfig", chatConfig);
        } else {
            String aid = request.getParameter("id");
            boolean needCheck = customerInfo == null && StringUtils.isNotBlank(aid);
            if (customerInfo != null) {
                String abcaid = customerInfo.getAbcaid();
                if (StringUtils.isNotBlank(aid) && !aid.equals(abcaid)) {
                    redisCache.clearLoginInfo(customerInfo);
                    needCheck = true;
                }
            }
            MemberAccountKeyInstance mebAut = redisCache.getMebAccount(keymark);
            String appSecret = mebAut.getAbcAppsecret();
            if (needCheck) {
                String reqParams = request.getQueryString();
                LoggerUtil.tehuiwebLog.info("ABC reqParams:" + reqParams);
                if (StringUtils.isNotBlank(reqParams)) {
                    AbcMallCheck mallCheck = new AbcMallCheck(appSecret, reqParams);
                    String signed = mallCheck.getSha256Sign();
                    Map paramsMap = mallCheck.getAllParameters();
                    String sign = (String) paramsMap.get("sign");
                    String id = (String) paramsMap.get("id");
                    citycode = (String) paramsMap.get("cityCode");
                    longitude = (String) paramsMap.get("longitude");
                    latitude = (String) paramsMap.get("latitude");
                    boolean isRight = StringUtils.equals(StringUtils.lowerCase(sign), StringUtils.lowerCase(signed));
                    if (isRight && id != null) {
                        AbcMallParam mallParam = new AbcMallParam();
                        mallParam.setRequestId(generageRequestId());
                        mallParam.setId(id);
                        mallParam.setCityCode(citycode);
                        mallParam.setLongitude(longitude);
                        mallParam.setLatitude(latitude);
                        PlainResult<CustomerInfoFTInstance> pr = loginManageService.loginByAbcaid(mallParam);
                        if (pr.isSuccess()) {
                            CustomerInfoFTInstance cumInfo = pr.getData();
                            cumInfo.setBdCityCode(citycode);
                            cumInfo.setLongitude(Double.valueOf(longitude));
                            cumInfo.setLatitude(Double.valueOf(latitude));
                            String uuidKey = redisCache.saveLoginUser(cumInfo);
                            Cookie cookie = new Cookie(Constants.COOKIE_TICKET, uuidKey);
                            cookie.setDomain(Constants.COOKIE_DOMAIN);
                            cookie.setPath("/");
                            cookie.setMaxAge(1 * 24 * 60 * 60);
                            response.addCookie(cookie);
                        }
                    }
                }
            } else if (customerInfo != null) {
                citycode = customerInfo.getBdCityCode();
                longitude = String.valueOf(customerInfo.getLongitude());
                latitude = String.valueOf(customerInfo.getLatitude());
                redisCache.delayLoginUserTime(customerInfo);
            }
        }
        Map<String, String> locaMap = new HashMap<String, String>();
        locaMap.put("keymark", keymark);
        locaMap.put("bdCityCode", citycode);
        locaMap.put("longitude", longitude);
        locaMap.put("latitude", latitude);
        model.addAttribute("locaObj", JSON.toJSONString(locaMap));
        return getNameSpace() + "mall";
    }

    public static void main(String[] args) {

        Map<String, String> locaMap = new HashMap<String, String>();
        locaMap.put("keymark", "missfresh");
        locaMap.put("bdCityCode", null);
        locaMap.put("longitude", "");
        locaMap.put("latitude", "");
        System.out.println(JSON.toJSONString(locaMap));
        //id=52sz60734ec2fb7eb881d96cbf9ada0535c5611aab0fb88
        //cityCode=179 131
        //longitude=116.395645&latitude=39
        //longitude=120.126639&latitude=30.282995
        //seqno=YH170607102722509588
        //appid=1BHR1EO1500FBA86E60A000085AC3C65
        //sign=33f46ba4d7243c774533b8ebaafc1d260a97f036d41089befcfb36afa3ea87aa
        //keymark=missfresh
        String appSecret = "1BHR1EO1500GBA86E60A0000AD73F4E6";
        String reqParams = "keymark=missfresh&id=55bc60734ec2fb7eb881d67juf9ada0535c5611aab0fw2c5&cityCode=131&longitude=116.345355&latitude=39.984665&seqno=YH170607102722525878&appid=1BHR1EO1500FBA86E60A000085AC3C65&sign=717cbe28bcdbd65d8d2dd4d81e123d1e7fe860cbe9982ae5a0f62cbfe999d1db";
        String reqxxxxxx = "keymark=missfresh&id=52sz60734ec2fb7eb881d96cbf9ada0535c5611aab0fb88&cityCode=179&longitude=120.126639&latitude=30.282995&seqno=YH170607102722509533&appid=1BHR1EO1500FBA86E60A000085AC3C65&sign=33f46ba4d7243c774533b8ebaafc1d260a97f036d41089befcfb36afa3ea87aa";
        AbcMallCheck mallCheck = new AbcMallCheck(appSecret, reqParams);
        String signed = mallCheck.getSha256Sign();
        System.out.println(signed);
    }

}
