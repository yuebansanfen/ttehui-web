/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.front.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.BaseResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.common.SystemConfig;
import com.mocentre.tehui.common.constant.ConfigConstant;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.ThirdNotifyDataFTInstance;
import com.mocentre.tehui.frontend.model.ThirdOrderResultFTInstance;
import com.mocentre.tehui.frontend.service.OrderManageService;
import com.mocentre.tehui.frontend.service.ThirdOrderManageService;

/**
 * 类NotifyController.java的实现描述：回调controller
 * 
 * @author sz.gong 2016年12月28日 下午3:05:19
 */
@Controller("notifyFTController")
@RequestMapping("/front/notify")
public class NotifyController extends BaseController {

    @Autowired
    private OrderManageService      orderMagService;
    @Autowired
    private ThirdOrderManageService thirdOrderMagService;

    /**
     * 特惠商城，农行支付回调（网页支付或者客户端支付通用）
     * 
     * @param request
     * @param response
     * @param model
     */
    @RequestMapping(value = "abcAsync.htm")
    public void abcAsync(HttpServletRequest request, HttpServletResponse response, Model model) {
        String tMerchantPage = "";
        String msg = request.getParameter("MSG");
        String succURL = SystemConfig.INSTANCE.getValue(ConfigConstant.ABC_RESULT_SUCCESS_URL);
        String failURL = SystemConfig.INSTANCE.getValue(ConfigConstant.ABC_RESULT_FAIL_URL);
        PrintWriter out = null;
        try {
            PlainResult<String> pr = orderMagService.abcAsyncNotify(msg);
            if (pr.isSuccess()) {
                String total = pr.getData();
                tMerchantPage = succURL + "?total=" + total;
            } else {
                tMerchantPage = failURL;
            }
            //model.addAttribute("tMerchantPage", tMerchantPage);
            response.setHeader("Cache-Control", "no-cache");
            out = response.getWriter();
            out.write("<html><head><meta http-equiv=\"refresh\" content=\"0; url='" + tMerchantPage
                    + "'\"></head><body><div style=\"display:none\"><URL>" + tMerchantPage
                    + "</URL></div></body></html>");
        } catch (Exception e) {
            LOGGER.error("abcAsync", e);
        } finally {
            if (out != null) {
                out.print("");
                out.close();
            }
        }
    }

    @RequestMapping(value = "resultSuccess.htm")
    public String resultSuccess(HttpServletRequest request, HttpServletResponse response, Model model) {
        String orderURL = SystemConfig.INSTANCE.getValue(ConfigConstant.ABC_ORDER_URL);
        try {
            String total = request.getParameter("total");
            model.addAttribute("total", total);
            model.addAttribute("orderUrl", orderURL);
        } catch (Exception e) {
            LOGGER.error("resultSuccess", e);
        }
        return getNameSpace() + "resultSuccess";
    }

    @RequestMapping(value = "resultFail.htm")
    public String resultFail(HttpServletRequest request, HttpServletResponse response, Model model) {
        String orderURL = SystemConfig.INSTANCE.getValue(ConfigConstant.ABC_ORDER_URL);
        try {
            model.addAttribute("orderUrl", orderURL);
        } catch (Exception e) {
            LOGGER.error("resultFail", e);
        }
        return getNameSpace() + "resultFail";
    }

    /**
     * 农行客户端支付结果
     * 
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "getAbcMallOrderResult.htm")
    public String getAbcMallOrderResult(HttpServletRequest request, HttpServletResponse response, Model model) {
        String paymentNum = request.getParameter("paymentNum");
        int index = paymentNum.indexOf(",");
        if (index > 0) {
            paymentNum = paymentNum.substring(0, index);
        }
        String orderURL = SystemConfig.INSTANCE.getValue(ConfigConstant.ABC_MALL_ORDER_URL);
        try {
            PlainResult<String> pr = orderMagService.getOrderResult(paymentNum, generageRequestId());
            model.addAttribute("orderUrl", orderURL);
            if (pr.isSuccess()) {
                String total = pr.getData();
                model.addAttribute("total", total);
                return getNameSpace() + "mallResultSuccess";
            }
        } catch (Exception e) {
            LOGGER.error("getAbcMallOrder", e);
        }
        return getNameSpace() + "mallResultFail";
    }

    /**
     * 农行客户端，农行支付回调（网页支付或者客户端支付通用）
     * 
     * @param request
     * @param response
     * @param model
     */
    @RequestMapping(value = "abcMallAsync.htm")
    public void abcMallAsync(HttpServletRequest request, HttpServletResponse response, Model model) {
        String tMerchantPage = "";
        String msg = request.getParameter("MSG");
        String succURL = SystemConfig.INSTANCE.getValue(ConfigConstant.ABC_MALL_RESULT_SUCCESS_URL);
        String failURL = SystemConfig.INSTANCE.getValue(ConfigConstant.ABC_MALL_RESULT_FAIL_URL);
        PrintWriter out = null;
        try {
            PlainResult<String> pr = orderMagService.abcAsyncNotify(msg);
            if (pr.isSuccess()) {
                String total = pr.getData();
                tMerchantPage = succURL + "?total=" + total;
            } else {
                tMerchantPage = failURL;
            }
            //model.addAttribute("tMerchantPage", tMerchantPage);
            response.setHeader("Cache-Control", "no-cache");
            out = response.getWriter();
            out.write("<html><head><meta http-equiv=\"refresh\" content=\"0; url='" + tMerchantPage
                    + "'\"></head><body><div style=\"display:none\"><URL>" + tMerchantPage
                    + "</URL></div></body></html>");
        } catch (Exception e) {
            LOGGER.error("abcMallAsync", e);
        } finally {
            if (out != null) {
                out.print("");
                out.close();
            }
        }
    }

    @Deprecated
    @RequestMapping(value = "mallResultSuccess.htm")
    public String mallResultSuccess(HttpServletRequest request, HttpServletResponse response, Model model) {
        String orderURL = SystemConfig.INSTANCE.getValue(ConfigConstant.ABC_MALL_ORDER_URL);
        try {
            String total = request.getParameter("total");
            model.addAttribute("total", total);
            model.addAttribute("orderUrl", orderURL);
        } catch (Exception e) {
            LOGGER.error("mallResultSuccess", e);
        }
        return getNameSpace() + "mallResultSuccess";
    }

    @Deprecated
    @RequestMapping(value = "mallResultFail.htm")
    public String mallResultFail(HttpServletRequest request, HttpServletResponse response, Model model) {
        String orderURL = SystemConfig.INSTANCE.getValue(ConfigConstant.ABC_MALL_ORDER_URL);
        try {
            model.addAttribute("orderUrl", orderURL);
        } catch (Exception e) {
            LOGGER.error("mallResultFail", e);
        }
        return getNameSpace() + "mallResultFail";
    }

    /**
     * 微信支付回调
     */
    @RequestMapping(value = "wx_async.htm", method = RequestMethod.POST)
    public void wxAsync(HttpServletRequest request, HttpServletResponse response) {
        String resData = "";
        StringBuffer bufData = new StringBuffer();
        BufferedReader in = null;
        String inputLine = null;
        try {
            in = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            while ((inputLine = in.readLine()) != null) {
                bufData.append(inputLine);
            }
            BaseResult br = orderMagService.wxAsyncNotify(bufData.toString());
            if (br.isSuccess()) {
                resData = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            } else {
                resData = "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
            }
        } catch (Exception e) {
            resData = "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
            LOGGER.error("wx_async", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        super.printXML(response, resData);
    }

    /**
     * 第三方微信支付回调
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "wxThirdAsync.htm")
    public void wxThirdAsync(HttpServletRequest request, HttpServletResponse response) {
        String resData = "";
        StringBuffer bufData = new StringBuffer();
        BufferedReader in = null;
        String inputLine = null;
        try {
            in = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            while ((inputLine = in.readLine()) != null) {
                bufData.append(inputLine);
            }
            BaseResult br = thirdOrderMagService.wxAsyncNotify(bufData.toString(), generageRequestId());
            if (br.isSuccess()) {
                resData = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            } else {
                resData = "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
            }
        } catch (Exception e) {
            resData = "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
            LOGGER.error("wxThirdAsync", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        super.printXML(response, resData);
    }

    /**
     * 第三方农行支付回调（网页支付或者客户端支付通用）
     * 
     * @param request
     * @param response
     * @param model
     */
    @RequestMapping(value = "abcThirdAsync.htm")
    public void abcThirdAsync(HttpServletRequest request, HttpServletResponse response, Model model) {
        String tMerchantPage = "", returnUrl = "";
        String totalMoney = "0";
        String msg = request.getParameter("MSG");
        String succURL = SystemConfig.INSTANCE.getValue(ConfigConstant.ABC_TD_RESULT_SUCCESS_URL);
        String failURL = SystemConfig.INSTANCE.getValue(ConfigConstant.ABC_TD_RESULT_FAIL_URL);
        PrintWriter out = null;
        try {
            PlainResult<ThirdNotifyDataFTInstance> pr = thirdOrderMagService.abcAsyncNotify(msg, generageRequestId());
            ThirdNotifyDataFTInstance resData = pr.getData();
            if (resData != null) {
                returnUrl = resData.getReturnUrl();
                totalMoney = resData.getTotalMoney();
                returnUrl = StringUtils.isBlank(returnUrl) ? returnUrl : URLEncoder.encode(returnUrl, "UTF-8");
            }
            if (pr.isSuccess()) {
                tMerchantPage = succURL + "?total=" + totalMoney + "&returnUrl=" + returnUrl;
            } else {
                tMerchantPage = failURL + "&returnUrl=" + returnUrl;
            }
            response.setHeader("Cache-Control", "no-cache");
            out = response.getWriter();
            out.write("<html><head><meta http-equiv=\"refresh\" content=\"0; url='" + tMerchantPage
                    + "'\"></head><body><div style=\"display:none\"><URL>" + tMerchantPage
                    + "</URL></div></body></html>");
        } catch (Exception e) {
            LOGGER.error("abcThirdAsync", e);
        } finally {
            if (out != null) {
                out.print("");
                out.close();
            }
        }
    }

    /**
     * 第三方农行客户端支付结果
     * 
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "getThirdOrderResult.htm")
    public String getThirdOrderResult(HttpServletRequest request, HttpServletResponse response, Model model) {
        String queryStr = request.getQueryString();
        if (StringUtils.isBlank(queryStr)) {
            model.addAttribute("errorMsg", "参数错误");
            return getErrorWap();
        }
        String orderNum = null, appKey = null;
        String[] paramsArr = queryStr.split(",");
        for (String param : paramsArr) {
            String[] params = param.split("=");
            if ("orderNum".equals(params[0])) {
                orderNum = params[1];
            } else if ("appKey".equals(params[0])) {
                appKey = params[1];
            }
        }
        if (StringUtils.isBlank(orderNum) || StringUtils.isBlank(appKey)) {
            model.addAttribute("errorMsg", "参数错误");
            return getErrorWap();
        }
        //String orderNum = request.getParameter("orderNum");
        // String appKey = request.getParameter("appKey");
        try {
            PlainResult<ThirdOrderResultFTInstance> pr = thirdOrderMagService.getOrderResult(orderNum, appKey,
                    generageRequestId());
            if (pr.isSuccess()) {
                ThirdOrderResultFTInstance orderRes = pr.getData();
                String resCode = orderRes.getResultCode();
                String returnUrl = orderRes.getReturnUrl();
                model.addAttribute("orderUrl", returnUrl);
                if ("SUCCESS".equals(resCode)) {
                    model.addAttribute("total", orderRes.getTotalMoney());
                    return getNameSpace() + "tdResultSuccess";
                } else {
                    return getNameSpace() + "tdResultFail";
                }
            }
            String msg = pr.getMessage();
            model.addAttribute("errorMsg", msg);
        } catch (Exception e) {
            LOGGER.error("getThirdOrderResult", e);
            model.addAttribute("errorMsg", "系统异常");
        }
        return getErrorWap();
    }

    /**
     * 第三方支付成功页面
     * 
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "tdResultSuccess.htm")
    public String thirdResultSuccess(HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            String total = request.getParameter("total");
            String returnUrl = request.getParameter("returnUrl");
            returnUrl = URLDecoder.decode(returnUrl, "utf-8");
            model.addAttribute("total", total);
            model.addAttribute("orderUrl", returnUrl);
        } catch (Exception e) {
            LOGGER.error("tdResultSuccess", e);
        }
        return getNameSpace() + "tdResultSuccess";
    }

    /**
     * 第三方支付失败页面
     * 
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "tdResultFail.htm")
    public String thirdResultFail(HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            String returnUrl = request.getParameter("returnUrl");
            returnUrl = URLDecoder.decode(returnUrl, "utf-8");
            model.addAttribute("orderUrl", returnUrl);
        } catch (Exception e) {
            LOGGER.error("tdResultFail", e);
        }
        return getNameSpace() + "tdResultFail";
    }

}
