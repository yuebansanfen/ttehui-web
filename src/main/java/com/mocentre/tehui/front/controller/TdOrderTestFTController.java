/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.front.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mocentre.common.ListResult;
import com.mocentre.commons.SignUtil;
import com.mocentre.commons.httpclient.HttpProtocolHandler;
import com.mocentre.commons.httpclient.HttpRequest;
import com.mocentre.commons.httpclient.HttpResponse;
import com.mocentre.commons.httpclient.HttpResultType;
import com.mocentre.tehui.common.util.LoggerUtil;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.AreasFTInstance;
import com.mocentre.tehui.frontend.param.ThirdOrderProductParam;
import com.mocentre.tehui.frontend.service.AreasManageService;

@Controller
@RequestMapping("/front/tdOrder")
public class TdOrderTestFTController extends BaseController {

    @Autowired
    private AreasManageService areasManageService;

    @RequestMapping(value = "index.htm")
    public String index(HttpServletRequest request, HttpServletResponse response) {
        return getNameSpace() + "index";
    }

    @RequestMapping(value = "payment.htm")
    public String payment(HttpServletRequest request, HttpServletResponse response) {
        String returnUrl = "";
        //StringBuffer payUrl = new StringBuffer();
        try {
            String appKey = "5795bMoyQBTYMNpKbR19Ggop8TR31jYk";
            String appSercet = "wFy3A14yrgCJ267DLgY7TiLfK4vDH70y";
            //            String appKey = "033004d487e5d18c442f227922822ee4";
            //            String appSercet = "0d5d0bc1694e8d3cbd0fab2d99b5d737";
            String orderNum = "test" + new Date().getTime();
            String orderAmount = "1";
            String orderDate = "2017/07/14";
            String orderTime = "11:05:00";
            String orderDetailJsonArr = "[{productId:1,productName:\"test\",productPrice:\"1\",productNum:1,productRemark:\"备注\"}]";
            String notifyUrl = "http://sptest.mocentre.cn/front/tdOrder/notify.htm";
            //            String notifyUrl = "http://shop.mocentre.com/front/tdOrder/notify.htm";
            Map<String, String> params = new HashMap<String, String>();
            params.put("payType", "abc_pay");
            params.put("orderNum", orderNum);
            params.put("orderAmount", orderAmount);
            params.put("orderDate", orderDate);
            params.put("orderTime", orderTime);
            params.put("orderDetailJsonArr", orderDetailJsonArr);
            //params.put("returnUrl", returnUrl);
            params.put("notifyUrl", notifyUrl);
            params.put("appKey", appKey);
            String mySign = SignUtil.buildMysign(appSercet, params);
            params.put("sign", mySign);
            //            payUrl.append("/front/thirdOrder/payments.htm");
            //            payUrl.append("?");
            //            payUrl.append("orderNum=" + orderNum);
            //            payUrl.append("&orderAmount=" + orderAmount);
            //            payUrl.append("&orderDate=" + orderDate);
            //            payUrl.append("&orderTime=" + orderTime);
            //            payUrl.append("&orderDetailJsonArr=" + URLEncoder.encode(orderDetailJsonArr, "UTF-8"));
            //            payUrl.append("&redirectUrl=" + redirectUrl);
            //            payUrl.append("&notifyUrl=" + notifyUrl);
            //            payUrl.append("&appKey=" + appKey);
            //            payUrl.append("&sign=" + mySign);
            HttpProtocolHandler handler = HttpProtocolHandler.getInstance();
            HttpRequest req = new HttpRequest(HttpResultType.STRING);
            req.setUrl("http://sptest.mocentre.cn/front/thirdOrder/payment.htm");
            //            req.setUrl("http://shop.mocentre.com/front/thirdOrder/payment.htm");
            req.setMethod(HttpRequest.METHOD_POST);
            req.setCharset("utf-8");
            req.setParameters(params);
            HttpResponse res = handler.execute(req);
            String resData = res.getStringResult();
            LoggerUtil.tehuiwebLog.info("时间：" + DateUtil.formatDate(new Date()) + " " + resData);
            JSONObject jObj = (JSONObject) JSONObject.parse(resData);
            returnUrl = jObj.getString("data");
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("payment", e);
        }
        return "redirect:" + returnUrl;
        //return "redirect:" + payUrl.toString();
    }

    @RequestMapping(value = "wxPayment.htm")
    public String wxPayment(HttpServletRequest request, HttpServletResponse response) {
        String returnUrl = "";
        //StringBuffer payUrl = new StringBuffer();
        try {
            //            String appKey = "5795bMoyQBTYMNpKbR19Ggop8TR31jYk";
            //            String appSercet = "wFy3A14yrgCJ267DLgY7TiLfK4vDH70y";
            String appKey = "033004d487e5d18c442f227922822ee4";
            String appSercet = "0d5d0bc1694e8d3cbd0fab2d99b5d737";
            String orderNum = "test" + new Date().getTime();
            String orderAmount = "1";
            String orderDate = "2017/07/14";
            String orderTime = "11:05:00";
            String orderDetailJsonArr = "[{productId:1,productName:\"test\",productPrice:\"1\",productNum:1,productRemark:\"备注\"}]";
            //String returnUrl = "https://sptest.mocentre.cn/front/tdOrder/result.htm?orderNum=" + orderNum;
            String notifyUrl = "http://shop.mocentre.com/front/tdOrder/notify.htm";
            Map<String, String> params = new HashMap<String, String>();
            params.put("payType", "wxpay");
            params.put("orderNum", orderNum);
            params.put("orderAmount", orderAmount);
            params.put("orderDate", orderDate);
            params.put("orderTime", orderTime);
            params.put("orderDetailJsonArr", orderDetailJsonArr);
            //params.put("returnUrl", returnUrl);
            params.put("notifyUrl", notifyUrl);
            params.put("appKey", appKey);
            String mySign = SignUtil.buildMysign(appSercet, params);
            params.put("sign", mySign);
            //            payUrl.append("/front/thirdOrder/payments.htm");
            //            payUrl.append("?");
            //            payUrl.append("orderNum=" + orderNum);
            //            payUrl.append("&orderAmount=" + orderAmount);
            //            payUrl.append("&orderDate=" + orderDate);
            //            payUrl.append("&orderTime=" + orderTime);
            //            payUrl.append("&orderDetailJsonArr=" + URLEncoder.encode(orderDetailJsonArr, "UTF-8"));
            //            payUrl.append("&redirectUrl=" + redirectUrl);
            //            payUrl.append("&notifyUrl=" + notifyUrl);
            //            payUrl.append("&appKey=" + appKey);
            //            payUrl.append("&sign=" + mySign);
            HttpProtocolHandler handler = HttpProtocolHandler.getInstance();
            HttpRequest req = new HttpRequest(HttpResultType.STRING);
            //req.setUrl("http://sptest.mocentre.cn/front/thirdOrder/payment.htm");
            req.setUrl("http://shop.mocentre.com/front/thirdOrder/payment.htm");
            req.setMethod(HttpRequest.METHOD_POST);
            req.setCharset("utf-8");
            req.setParameters(params);
            HttpResponse res = handler.execute(req);
            String resData = res.getStringResult();
            LoggerUtil.tehuiwebLog.info("时间：" + DateUtil.formatDate(new Date()) + " " + resData);
            JSONObject jObj = (JSONObject) JSONObject.parse(resData);
            returnUrl = jObj.getString("data");
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("payment", e);
        }
        return "redirect:" + returnUrl;
        //return "redirect:" + payUrl.toString();
    }

    @RequestMapping(value = "notify.htm")
    public void notify(HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            //String appSercet = "wFy3A14yrgCJ267DLgY7TiLfK4vDH70y";
            String appSercet = "0d5d0bc1694e8d3cbd0fab2d99b5d737";
            String orderNum = request.getParameter("orderNum");
            String appKey = request.getParameter("appKey");
            String status = request.getParameter("status");
            String sign = request.getParameter("sign");
            Map<String, String> params = new HashMap<String, String>();
            params.put("orderNum", orderNum);
            params.put("appKey", appKey);
            params.put("status", status);
            params.put("sign", sign);
            String mySign = SignUtil.buildMysign(appSercet, params);
            LoggerUtil.tehuiwebLog.info("notify时间：" + DateUtil.formatDate(new Date()) + " " + mySign + " " + sign);
            LoggerUtil.tehuiwebLog.info("orderNum：" + orderNum + "  appKey:" + appKey + " status:" + status);
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("notify", e);
        }
    }

    @RequestMapping(value = "result.htm")
    public String result(HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            //            String appKey = "5795bMoyQBTYMNpKbR19Ggop8TR31jYk";
            //            String appSercet = "wFy3A14yrgCJ267DLgY7TiLfK4vDH70y";
            String appKey = "033004d487e5d18c442f227922822ee4";
            String appSercet = "0d5d0bc1694e8d3cbd0fab2d99b5d737";
            String orderNum = request.getParameter("orderNum");
            Map<String, String> params = new HashMap<String, String>();
            params.put("orderNum", orderNum);
            params.put("appKey", appKey);
            String mySign = SignUtil.buildMysign(appSercet, params);
            params.put("sign", mySign);
            HttpProtocolHandler handler = HttpProtocolHandler.getInstance();
            HttpRequest req = new HttpRequest(HttpResultType.STRING);
            //req.setUrl("http://sptest.mocentre.cn/front/thirdOrder/query.htm");
            req.setUrl("http://shop.mocentre.com/front/thirdOrder/query.htm");
            req.setMethod(HttpRequest.METHOD_POST);
            req.setCharset("utf-8");
            req.setParameters(params);
            HttpResponse res = handler.execute(req);
            String resData = res.getStringResult();
            LoggerUtil.tehuiwebLog.info("时间：" + DateUtil.formatDate(new Date()) + " " + resData);
            model.addAttribute("resData", resData);
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("result", e);
        }
        return getNameSpace() + "result";
    }

    public static void main(String[] args) {
        String appSercet = "wFy3A14yrgCJ267DLgY7TiLfK4vDH70y";
        String orderNum = "test1500272814506";
        String orderAmount = "1";
        String orderDate = "2017/07/14";
        String orderTime = "11:05:00";
        String orderDetailJsonArr = "[{productId:1,productName:\"test\",productPrice:\"1\",productNum:1,productRemark:\"备注\"}]";
        String appKey = "5795bMoyQBTYMNpKbR19Ggop8TR31jYk";
        String redirectUrl = "http://sptest.mocentre.cn/front/tdOrder/result.htm";
        String notifyUrl = "http://sptest.mocentre.cn/front/tdOrder/notify.htm";
        String sign = "a450f2b1d658184a2a5e25d8204cf234e41b948e4c33ca44b97ba56e25fe2fa8";
        Map<String, String> params = new HashMap<String, String>();
        params.put("orderNum", orderNum);
        params.put("orderAmount", orderAmount);
        params.put("orderDate", orderDate);
        params.put("orderTime", orderTime);
        params.put("orderDetailJsonArr", orderDetailJsonArr);
        params.put("redirectUrl", redirectUrl);
        params.put("notifyUrl", notifyUrl);
        params.put("appKey", appKey);
        params.put("sign", sign);
        String mySign = SignUtil.buildMysign(appSercet, params);
        System.out.println(mySign);
        List<ThirdOrderProductParam> productList = JSONArray.parseArray(orderDetailJsonArr,
                ThirdOrderProductParam.class);
        System.out.println(productList.size());
    }

    @RequestMapping(value = "testAreas.htm", method = RequestMethod.GET)
    public void testAreas(HttpServletRequest request, HttpServletResponse response) {
        ListResult<AreasFTInstance> result = new ListResult<AreasFTInstance>();
        try {
            ListResult<AreasFTInstance> cityResultList = areasManageService.getCityList(generageRequestId());
            List<AreasFTInstance> cityList = cityResultList.getData();
            result.setData(cityList);
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    @RequestMapping(value = "testRedirect.htm")
    public String testRedirect(HttpServletRequest request, HttpServletResponse response) {

        return "redirect:" + "/front/tdOrder/redirectHttps.htm";
    }

    @RequestMapping(value = "redirectHttps.htm")
    public String redirectHttps(HttpServletRequest request, HttpServletResponse response, Model model) {
        model.addAttribute("errorMsg", "是否https");
        return getErrorWap();
    }

}
