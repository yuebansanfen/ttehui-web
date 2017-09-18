/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.front.controller;

import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONArray;
import com.mocentre.common.BaseResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.common.SystemConfig;
import com.mocentre.tehui.common.constant.ConfigConstant;
import com.mocentre.tehui.common.constant.Constants;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.common.util.CookieUtil;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.OrderCashFTInstance;
import com.mocentre.tehui.frontend.model.OrderQueryDataFTInstance;
import com.mocentre.tehui.frontend.model.ThirdPayDataFTInstance;
import com.mocentre.tehui.frontend.model.WxPayDataFTInstance;
import com.mocentre.tehui.frontend.param.ThirdOrderParam;
import com.mocentre.tehui.frontend.param.ThirdOrderProductParam;
import com.mocentre.tehui.frontend.service.ThirdOrderManageService;

/**
 * 类ThirdOrderFTController.java的实现描述：第三方订单
 * 
 * @author sz.gong 2017年6月20日 下午1:56:03
 */
@Controller
@RequestMapping("/front/thirdOrder")
public class ThirdOrderFTController extends BaseController {

    @Autowired
    private ThirdOrderManageService thirdOrderMangerService;
    @Autowired
    private RedisCache              redisCache;

    @RequestMapping(value = "payment.htm", method = RequestMethod.POST)
    public void payment(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<String> pr = new PlainResult<String>();
        try {
            String payType = request.getParameter("payType");
            String orderNum = request.getParameter("orderNum");
            String orderAmount = request.getParameter("orderAmount");
            String orderDate = request.getParameter("orderDate");
            String orderTime = request.getParameter("orderTime");
            String abcaid = request.getParameter("abcaid");
            String appKey = request.getParameter("appKey");
            String resultUrl = request.getParameter("resultUrl");
            String notifyUrl = request.getParameter("notifyUrl");
            String orderDetailJsonArr = request.getParameter("orderDetailJsonArr");
            String reqId = request.getParameter("requestId");
            boolean paramValid = StringUtils.isBlank(payType) || StringUtils.isBlank(orderNum)
                    || StringUtils.isBlank(orderAmount) || StringUtils.isBlank(orderDate)
                    || StringUtils.isBlank(orderTime) || StringUtils.isBlank(appKey) || StringUtils.isBlank(notifyUrl);
            if (paramValid) {
                pr.setErrorMessage("1000", "参数错误");
            }
            boolean isMatch = CommUtil.isPositiveNumber(orderAmount);
            if (!isMatch) {
                pr.setErrorMessage("1000", "参数错误");
            }
            if (pr.isSuccess()) {
                List<ThirdOrderProductParam> productList = null;
                if (StringUtils.isNotBlank(orderDetailJsonArr)) {
                    productList = JSONArray.parseArray(orderDetailJsonArr, ThirdOrderProductParam.class);
                }
                if (StringUtils.isBlank(resultUrl)) {
                    resultUrl = SystemConfig.INSTANCE.getValue(ConfigConstant.ABC_TD_RESULT_URL);
                    resultUrl += "?orderNum=" + orderNum + "&appKey=" + appKey;
                }
                ThirdOrderParam orderParam = new ThirdOrderParam();
                orderParam.setPayType(payType);
                orderParam.setProductList(productList);
                orderParam.setOrderNum(orderNum);
                orderParam.setOrderAmount(Long.parseLong(orderAmount));
                orderParam.setOrderDate(orderDate);
                orderParam.setOrderTime(orderTime);
                orderParam.setAbcaid(abcaid);
                orderParam.setAppKey(appKey);
                orderParam.setResultUrl(resultUrl);
                orderParam.setNotifyUrl(notifyUrl);
                orderParam.setRequestId(generageRequestId());
                pr = thirdOrderMangerService.orderPayment(orderParam);
            }
        } catch (Exception e) {
            pr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, pr.toJsonString());
    }

    @RequestMapping(value = "wxPay.htm", method = RequestMethod.GET)
    public String wxPay(HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            String orderNum = request.getParameter("orderNum");
            String appKey = request.getParameter("appKey");
            SystemConfig config = SystemConfig.INSTANCE;
            String appid = config.getValue(ConfigConstant.WX_APPID);
            String callback = config.getValue(ConfigConstant.WX_TD_CALLBACK_URL);
            String oauthCodeUrl = config.getValue(ConfigConstant.WX_OAUTH2_CODE_URL);
            String openid = CookieUtil.getCookieValue(request, Constants.COOKIE_USER_OPENID);
            if (StringUtils.isBlank(openid)) {
                StringBuffer buf = new StringBuffer();
                buf.append(oauthCodeUrl);
                buf.append("?appid=" + appid);
                callback = callback + "?appKey=" + appKey + "&orderNum=" + orderNum;
                callback = URLEncoder.encode(callback, "utf-8");
                buf.append("&redirect_uri=" + callback);
                buf.append("&response_type=code");
                buf.append("&scope=snsapi_base");
                //buf.append("&state=" + paymentNum);
                buf.append("#wechat_redirect");
                return "redirect:" + buf.toString();
            } else {
                String cxtPath = request.getContextPath();
                String path = "https://" + request.getServerName() + cxtPath;
                String mySign = DigestUtils.md5Hex("appKey=" + appKey + "orderNum=" + orderNum);
                return "redirect:" + path + "/front/thirdOrder/wxDetail.htm?orderNum=" + orderNum + "&appKey=" + appKey
                        + "&sign=" + mySign;
            }
        } catch (Exception e) {
            LOGGER.error("wxPay", e);
        }
        model.addAttribute("errorMsg", "支付错误");
        return getErrorWap();
    }

    @RequestMapping(value = "wxDetail.htm", method = RequestMethod.GET)
    public String wxDetail(HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            String base = request.getContextPath();
            String orderNum = request.getParameter("orderNum");
            String appKey = request.getParameter("appKey");
            String sign = request.getParameter("sign");
            String openid = CookieUtil.getCookieValue(request, Constants.COOKIE_USER_OPENID);
            if (StringUtils.isBlank(orderNum) || StringUtils.isBlank(appKey) || StringUtils.isBlank(openid)) {
                model.addAttribute("errorMsg", "参数错误");
                return getErrorWap();
            }
            String mySign = DigestUtils.md5Hex("appKey=" + appKey + "orderNum=" + orderNum);
            if (!mySign.equals(sign)) {
                model.addAttribute("errorMsg", "签名错误");
                return getErrorWap();
            }
            String ipAdds = CommUtil.getIpAddr(request);
            PlainResult<ThirdPayDataFTInstance> pr = thirdOrderMangerService.getOrderPayData(orderNum, appKey, ipAdds,
                    openid, generageRequestId());
            if (!pr.isSuccess()) {
                model.addAttribute("errorMsg", pr.getMessage());
                return getErrorWap();
            }
            ThirdPayDataFTInstance data = pr.getData();
            OrderCashFTInstance orderCash = data.getOrderCash();
            WxPayDataFTInstance wxPayData = data.getWxPayData();
            String returnUrl = data.getReturnUrl();
            model.addAttribute("orderDetail", orderCash);
            model.addAttribute("wxPayData", wxPayData);
            model.addAttribute("base", base);
            model.addAttribute("returnUrl", URLEncoder.encode(returnUrl, "UTF-8"));
        } catch (Exception e) {
            LOGGER.error("wxDetail", e);
            model.addAttribute("errorMsg", "系统异常");
            return getErrorWap();
        }
        return getNameSpace() + "detail";
    }

    //    /**
    //     * 订单支付
    //     * 
    //     * @param request
    //     * @param response
    //     */
    //    @RequestMapping(value = "payments.htm")
    //    public String payments(HttpServletRequest request, HttpServletResponse response) {
    //        PlainResult<String> pr = new PlainResult<String>();
    //        try {
    //            String orderNum = request.getParameter("orderNum");
    //            String orderAmount = request.getParameter("orderAmount");
    //            String orderDate = request.getParameter("orderDate");
    //            String orderTime = request.getParameter("orderTime");
    //            String abcaid = request.getParameter("abcaid");
    //            String appKey = request.getParameter("appKey");
    //            String resultUrl = request.getParameter("resultUrl");
    //            String notifyUrl = request.getParameter("notifyUrl");
    //            String orderDetailJsonArr = request.getParameter("orderDetailJsonArr");
    //            boolean paramValid = StringUtils.isBlank(orderNum) || StringUtils.isBlank(orderAmount)
    //                    || StringUtils.isBlank(orderDate) || StringUtils.isBlank(orderTime) || StringUtils.isBlank(appKey)
    //                    || StringUtils.isBlank(notifyUrl) || StringUtils.isBlank(orderDetailJsonArr);
    //            if (paramValid) {
    //                pr.setErrorMessage("1000", "参数错误");
    //            }
    //            boolean isMatch = CommUtil.isPositiveNumber(orderAmount);
    //            if (!isMatch) {
    //                pr.setErrorMessage("1000", "参数错误");
    //            }
    //            if (pr.isSuccess()) {
    //                orderDetailJsonArr = URLDecoder.decode(orderDetailJsonArr, "UTF-8");
    //                List<ThirdOrderProductParam> productList = JSONArray.parseArray(orderDetailJsonArr,
    //                        ThirdOrderProductParam.class);
    //                ThirdOrderParam orderParam = new ThirdOrderParam();
    //                orderParam.setProductList(productList);
    //                orderParam.setOrderNum(orderNum);
    //                orderParam.setOrderAmount(Long.parseLong(orderAmount));
    //                orderParam.setOrderDate(orderDate);
    //                orderParam.setOrderTime(orderTime);
    //                orderParam.setAbcaid(abcaid);
    //                orderParam.setAppKey(appKey);
    //                orderParam.setResultUrl(resultUrl);
    //                orderParam.setNotifyUrl(notifyUrl);
    //                orderParam.setRequestId(generageRequestId());
    //                pr = thirdOrderMangerService.orderPayment(orderParam);
    //            }
    //        } catch (Exception e) {
    //            pr.setErrorMessage("999", "系统异常");
    //        }
    //        String redUrl = pr.getData();
    //        return "redirect:" + redUrl;
    //    }

    /**
     * 订单查询
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "query.htm", method = RequestMethod.POST)
    public void orderQuery(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<OrderQueryDataFTInstance> pr = new PlainResult<OrderQueryDataFTInstance>();
        try {
            String orderNum = request.getParameter("orderNum");
            String appKey = request.getParameter("appKey");
            boolean paramValid = StringUtils.isBlank(orderNum) || StringUtils.isBlank(appKey);
            if (paramValid) {
                pr.setErrorMessage("1000", "参数错误");
            }
            if (pr.isSuccess()) {
                pr = thirdOrderMangerService.orderQuery(orderNum, appKey, generageRequestId());
            }
        } catch (Exception e) {
            pr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 退款
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "refund.htm", method = RequestMethod.POST)
    public void orderrefund(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            String orderNum = request.getParameter("orderNum");
            String appKey = request.getParameter("appKey");
            String trxAmount = request.getParameter("trxAmount");
            boolean paramValid = StringUtils.isBlank(orderNum) || StringUtils.isBlank(appKey)
                    || StringUtils.isBlank(trxAmount);
            if (paramValid) {
                br.setErrorMessage("1000", "参数错误");
            }
            boolean isMatch = CommUtil.isPositiveNumber(trxAmount);
            if (!isMatch) {
                br.setErrorMessage("1000", "参数错误");
            }
            Long mTrxAmount = Long.parseLong(trxAmount);
            if (br.isSuccess()) {
                br = thirdOrderMangerService.orderRefund(orderNum, appKey, mTrxAmount, generageRequestId());
            }
        } catch (Exception e) {
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "error.htm")
    public String error(HttpServletRequest request, HttpServletResponse response, Model model) {
        String errorCode = request.getParameter("errorCode");
        String errorMsg = "操作失败";
        if ("501".equals(errorCode)) {
            errorMsg = "无效的appKey";
        } else if ("500".equals(errorCode)) {
            errorMsg = "签名失败";
        }
        model.addAttribute("errorMsg", errorMsg);
        return getErrorWap();
    }

}
