/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.front.controller;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.common.SystemConfig;
import com.mocentre.tehui.common.constant.ConfigConstant;
import com.mocentre.tehui.common.constant.Constants;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.common.util.CookieUtil;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.ApplyRefundFTInstance;
import com.mocentre.tehui.frontend.model.CashOrderFTInstance;
import com.mocentre.tehui.frontend.model.CustomerInfoFTInstance;
import com.mocentre.tehui.frontend.model.OrderCashFTInstance;
import com.mocentre.tehui.frontend.model.OrderDetailFTInstance;
import com.mocentre.tehui.frontend.model.OrderFTInstance;
import com.mocentre.tehui.frontend.model.PayOrderFTInstance;
import com.mocentre.tehui.frontend.model.WxPayDataFTInstance;
import com.mocentre.tehui.frontend.param.ApplyRefundParam;
import com.mocentre.tehui.frontend.param.CashGoodsParam;
import com.mocentre.tehui.frontend.param.CashierParam;
import com.mocentre.tehui.frontend.param.OrderBillParam;
import com.mocentre.tehui.frontend.param.OrderPayParam;
import com.mocentre.tehui.frontend.param.OrderQueryParam;
import com.mocentre.tehui.frontend.param.SubmitOrderParam;
import com.mocentre.tehui.frontend.service.OrderManageService;

/**
 * 类OrderFTController.java的实现描述：订单controller
 * 
 * @author sz.gong 2016年12月23日 下午2:32:13
 */
@Controller
@RequestMapping("/front/order")
public class OrderFTController extends BaseController {

    @Autowired
    private OrderManageService orderMagService;
    @Autowired
    private RedisCache         redisCache;

    /**
     * 商品详情页-立即购买
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "buy_now.htm", method = RequestMethod.POST)
    public void buyNow(HttpServletRequest request, HttpServletResponse response) {

        PlainResult<CashOrderFTInstance> result = new PlainResult<CashOrderFTInstance>();
        try {
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            CashierParam param = super.bindClass(request, CashierParam.class);
            param.setRequestId(generageRequestId());
            boolean paramValid = StringUtils.isBlank(param.getGoodsSku()) || param.getBuyNum() == null;
            paramValid = param.getGoodsId() == null && param.getActGoodsId() == null;//商品和活动商品选其一
            if (paramValid) {
                result.setErrorMessage("1000", "参数错误");
            }
            if (userInfo == null) {
                result.setErrorMessage("1001", "用户不存在");
            }
            if (result.isSuccess()) {
                param.setCustomerId(userInfo.getId());
                result = orderMagService.submitToCashier(param);
            }
        } catch (Exception e) {
            LOGGER.error("buyNow", e);
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 收银台提交订单（针对订单未生成的）
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "pay_order.htm", method = RequestMethod.POST)
    public void payOrder(HttpServletRequest request, HttpServletResponse response) {

        PlainResult<String> result = new PlainResult<String>();
        try {
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            if (userInfo == null) {
                result.setErrorMessage("1001", "用户不存在");
            }
            if (result.isSuccess()) {
                String cashGoodsJArrStr = request.getParameter("cashGoodsJsonArr");
                String billJson = request.getParameter("billJson");
                SubmitOrderParam payParam = super.bindClass(request, SubmitOrderParam.class);
                payParam.setRequestId(generageRequestId());
                List<CashGoodsParam> cashGoodsList = new ArrayList<CashGoodsParam>();
                JSONArray cashGoodsJArr = JSONArray.parseArray(cashGoodsJArrStr);
                for (int i = 0; i < cashGoodsJArr.size(); i++) {
                    JSONObject cashGoodsJObj = cashGoodsJArr.getJSONObject(i);
                    CashGoodsParam cashGoods = JSON.toJavaObject(cashGoodsJObj, CashGoodsParam.class);
                    cashGoodsList.add(cashGoods);
                }
                payParam.setCashGoodsList(cashGoodsList);
                if (StringUtils.isBlank(billJson)) {
                    payParam.setBill(null);
                } else {
                    JSONObject billJobj = JSON.parseObject(billJson);
                    if (billJobj.isEmpty()) {
                        payParam.setBill(null);
                    } else {
                        OrderBillParam billParam = JSON.toJavaObject(billJobj, OrderBillParam.class);
                        payParam.setBill(billParam);
                    }
                }
                payParam.setCustomerId(userInfo.getId());
                result = orderMagService.submitOrder(payParam);
            }
        } catch (Exception e) {
            LOGGER.error("payOrder", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 跳转到支付页面
     * 
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "payment.htm")
    public String payment(HttpServletRequest request, HttpServletResponse response, Model model) {
        //接口请求域名
        String baseUrl = SystemConfig.INSTANCE.getValue(ConfigConstant.VUE_SHOP_BASE_URL);
        String paymentNum = request.getParameter("paymentNum");
        String flag = request.getParameter("flag");
        String redirectUrl = request.getParameter("redirectUrl");
        PlainResult<PayOrderFTInstance> result = orderMagService.payment(paymentNum, flag, generageRequestId());
        PayOrderFTInstance order = result.getData();
        if (result.isSuccess()) {
            Boolean havePayed = order.getHavePayed();
            BigDecimal totalMoney = order.getTotalMoney();
            //针对已经支付或者支付金额为0，无需跳转到第三方支付
            if (havePayed || totalMoney.compareTo(new BigDecimal(0)) == 0) {//直接跳转到支付成功页面
                if ("1".equals(flag)) {//商城
                    return "redirect:" + baseUrl + "/front/notify/resultSuccess.htm?total=" + totalMoney;
                } else {//农行客户端
                    return "redirect:" + baseUrl + "/front/notify/mallResultSuccess.htm?total=" + totalMoney;
                }
            } else {//跳到支付页面
                String url = order.getPaymentURL();
                if ("1".equals(flag)) {
                    return "redirect:" + url;
                }
                String returnUrl = baseUrl + "/front/notify/getAbcMallOrderResult.htm?paymentNum=" + paymentNum;
                String token = url.substring(url.indexOf("TOKEN") + 6, url.length());
                String nonUrl = "https://mocentre_pay/abcpay%7C" + token + "&1110&new*" + returnUrl;
                return "redirect:" + nonUrl;
            }
        } else {
            model.addAttribute("errorMsg", order.getErrorMessage());
            model.addAttribute("redirectUrl", redirectUrl);
            return getErrorWap();
        }
    }

    /**
     * 我的订单列表
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "order_list.htm", method = RequestMethod.POST)
    public void orderList(HttpServletRequest request, HttpServletResponse response) {

        BaseResult result = new ListResult<OrderFTInstance>();
        try {
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            if (userInfo == null) {
                result.setErrorMessage("1001", "用户不存在");
            }
            if (result.isSuccess()) {
                OrderQueryParam orderParam = super.bindClass(request, OrderQueryParam.class);
                orderParam.setCustomerId(userInfo.getId());
                orderParam.setRequestId(generageRequestId());
                result = orderMagService.orderList(orderParam);
            }
        } catch (Exception e) {
            LOGGER.error("orderList", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 订单详情
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "order_detail.htm", method = RequestMethod.POST)
    public void orderDetail(HttpServletRequest request, HttpServletResponse response) {

        BaseResult result = new PlainResult<OrderDetailFTInstance>();
        try {
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            if (userInfo == null) {
                result.setErrorMessage("1001", "用户不存在");
            }
            if (result.isSuccess()) {
                String orderNum = request.getParameter("orderNum");
                result = orderMagService.orderDetail(userInfo.getId(), orderNum, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("orderDetail", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 确认收货
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "order_confirm.htm", method = RequestMethod.POST)
    public void orderConfirm(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        try {
            String orderNum = request.getParameter("orderNum");
            if (StringUtils.isBlank(orderNum)) {
                result.setErrorMessage("1000", "参数错误");
            }
            if (result.isSuccess()) {
                CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
                Long customerId = userInfo.getId();
                result = orderMagService.orderConfirm(orderNum, customerId, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("orderConfirm", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 未支付订单重新提交支付
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "order_submit.htm", method = RequestMethod.POST)
    public void orderSubmit(HttpServletRequest request, HttpServletResponse response) {

        PlainResult<CashOrderFTInstance> result = new PlainResult<CashOrderFTInstance>();
        try {
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            if (userInfo == null) {
                result.setErrorMessage("1001", "用户不存在");
            }
            if (result.isSuccess()) {
                String orderNum = request.getParameter("orderNum");
                result = orderMagService.orderSubmit(orderNum, userInfo.getId(), generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("orderDetail", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 收银台提交订单（针对订单已生成的）
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "order_pay.htm", method = RequestMethod.POST)
    public void orderPay(HttpServletRequest request, HttpServletResponse response) {

        PlainResult<String> result = new PlainResult<String>();
        try {
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            if (userInfo == null) {
                result.setErrorMessage("1001", "用户不存在");
            }
            OrderPayParam payParam = super.bindClass(request, OrderPayParam.class);
            String orderNum = payParam.getOrderNum();
            payParam.setCustomerId(userInfo.getId());
            if (StringUtils.isBlank(orderNum)) {
                result.setErrorMessage("1002", "订单不存在");
            }
            String billJson = request.getParameter("billJson");
            if (result.isSuccess()) {
                if (StringUtils.isBlank(billJson)) {
                    payParam.setBill(null);
                } else {
                    JSONObject billJobj = JSON.parseObject(billJson);
                    if (billJobj.isEmpty()) {
                        payParam.setBill(null);
                    } else {
                        OrderBillParam billParam = JSON.toJavaObject(billJobj, OrderBillParam.class);
                        payParam.setBill(billParam);
                    }
                }
                result = orderMagService.orderPay(payParam);
            }
        } catch (Exception e) {
            LOGGER.error("orderPay", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 删除订单
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "delete_order.htm", method = RequestMethod.POST)
    public void deleteOrder(HttpServletRequest request, HttpServletResponse response) {

        BaseResult result = new BaseResult();
        String orderNum = request.getParameter("orderNum");
        try {
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            if (StringUtils.isBlank(orderNum)) {
                result.setErrorMessage("1000", "参数错误");
            }
            if (userInfo == null) {
                result.setErrorMessage("1001", "用户不存在");
            }
            if (result.isSuccess()) {
                result = orderMagService.deleteOrder(orderNum, userInfo.getId(), generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("deleteOrder", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 申请售后
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "preview_apply.htm", method = RequestMethod.POST)
    public void previewApplyOrder(HttpServletRequest request, HttpServletResponse response) {

        BaseResult pr = new PlainResult<ApplyRefundFTInstance>();
        String orderNum = request.getParameter("orderNum");
        String orderDetailNum = request.getParameter("orderDetailNum");
        try {
            if (StringUtils.isBlank(orderNum) || StringUtils.isBlank(orderDetailNum)) {
                pr.setErrorMessage("1000", "参数错误");
            }
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            if (userInfo == null) {
                pr.setErrorMessage("1001", "用户不存在");
            }
            if (pr.isSuccess()) {
                Long customerId = userInfo.getId();
                pr = orderMagService.previewOrderRefund(orderNum, orderDetailNum, customerId, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("previewApplyOrder", e);
            pr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 提交申请
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "refund_apply.htm", method = RequestMethod.POST)
    public void refundApplyOrder(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        ApplyRefundParam refundParam = super.bindClass(request, ApplyRefundParam.class);
        String orderNum = refundParam.getOrderNum();
        String orderDetailNum = refundParam.getOrderDetailNum();
        String refReason = refundParam.getRefundReason();
        try {
            boolean paramValid = StringUtils.isBlank(orderNum) || StringUtils.isBlank(orderDetailNum)
                    || StringUtils.isBlank(refReason);
            if (paramValid) {
                br.setErrorMessage("1000", "参数错误");
            }
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            if (userInfo == null) {
                br.setErrorMessage("1001", "用户不存在");
            }
            if (br.isSuccess()) {
                refundParam.setCustomerId(userInfo.getId());
                refundParam.setRequestId(generageRequestId());
                br = orderMagService.applyOrderRefund(refundParam);
            }
        } catch (Exception e) {
            LOGGER.error("refundApplyOrder", e);
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 撤销申请
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "revoke_apply.htm", method = RequestMethod.POST)
    public void revokeApplyOrder(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        String orderNum = request.getParameter("orderNum");
        String orderDetailNum = request.getParameter("orderDetailNum");
        try {
            if (StringUtils.isBlank(orderNum) || StringUtils.isBlank(orderDetailNum)) {
                br.setErrorMessage("1000", "参数错误");
            }
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            if (userInfo == null) {
                br.setErrorMessage("1001", "用户不存在");
            }
            if (br.isSuccess()) {
                Long customerId = userInfo.getId();
                br = orderMagService.revokeOrderRefund(orderNum, orderDetailNum, customerId, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("revoke_apply", e);
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 获取用户openid
     * 
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "wx_oauth.htm", method = RequestMethod.GET)
    public String wxOAuth(HttpServletRequest request, HttpServletResponse response, Model model) {
        String paymentNum = request.getParameter("paymentNum");
        SystemConfig config = SystemConfig.INSTANCE;
        String appid = config.getValue(ConfigConstant.WX_APPID);
        String callback = config.getValue(ConfigConstant.WX_CALLBACK_URL);
        String oauthCodeUrl = config.getValue(ConfigConstant.WX_OAUTH2_CODE_URL);
        String cashUrl = config.getValue(ConfigConstant.WX_CASH_URL);
        try {
            if (StringUtils.isBlank(paymentNum)) {
                model.addAttribute("errorMsg", "参数错误");
                return getErrorWap();
            }
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            if (userInfo == null) {
                model.addAttribute("errorMsg", "用户不存在");
                return getErrorWap();
            }
            Long customerId = userInfo.getId();
            PlainResult<OrderCashFTInstance> pr = orderMagService.getOrderCashDetail(paymentNum, customerId,
                    generageRequestId());
            if (pr.isSuccess()) {
                OrderCashFTInstance orderCash = pr.getData();
                BigDecimal totalMoney = orderCash.getTotalMoney();
                if (totalMoney.compareTo(new BigDecimal(0)) == 0) {//直接跳转到支付成功页面
                    return "redirect:/front/notify/resultSuccess.htm?total=" + totalMoney;
                }
            }
            StringBuffer buf = new StringBuffer();
            buf.append(oauthCodeUrl);
            buf.append("?appid=" + appid);
            callback = URLEncoder.encode(callback, "utf-8");
            buf.append("&redirect_uri=" + callback);
            buf.append("&response_type=code");
            buf.append("&scope=snsapi_base");
            buf.append("&state=" + paymentNum);
            buf.append("#wechat_redirect");
            String openid = CookieUtil.getCookieValue(request, Constants.COOKIE_USER_OPENID);
            if (StringUtils.isBlank(openid)) {
                return "redirect:" + buf.toString();
            } else {
                cashUrl = cashUrl + "?paymentNum=" + paymentNum;
                return "redirect:" + cashUrl;
            }
        } catch (Exception e) {
            LOGGER.error("wx_oauth", e);
        }
        model.addAttribute("errorMsg", "支付错误");
        return getErrorWap();
    }

    /**
     * 获取详情
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "cash_detail.htm", method = RequestMethod.POST)
    public void cashDetail(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<OrderCashFTInstance> pr = new PlainResult<OrderCashFTInstance>();
        String paymentNum = request.getParameter("paymentNum");
        try {
            if (StringUtils.isBlank(paymentNum)) {
                pr.setErrorMessage("1000", "参数错误");
            }
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            if (userInfo == null) {
                pr.setErrorMessage("1001", "用户不存在");
            }
            if (pr.isSuccess()) {
                Long customerId = userInfo.getId();
                pr = orderMagService.getOrderCashDetail(paymentNum, customerId, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("cash_detail", e);
            pr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 微信支付
     */
    @RequestMapping(value = "wx_payment.htm", method = RequestMethod.POST)
    public void wxPayment(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<WxPayDataFTInstance> pr = new PlainResult<WxPayDataFTInstance>();
        String ipAdds = CommUtil.getIpAddr(request);
        String paymentNum = request.getParameter("paymentNum");
        try {
            if (StringUtils.isBlank(paymentNum)) {
                pr.setErrorMessage("1000", "参数错误");
            }
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            if (userInfo == null) {
                pr.setErrorMessage("1001", "用户不存在");
            }
            String openid = CookieUtil.getCookieValue(request, Constants.COOKIE_USER_OPENID);
            if (pr.isSuccess()) {
                Long customerId = userInfo.getId();
                pr = orderMagService.wxPayment(paymentNum, customerId, openid, ipAdds, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("wx_payment", e);
            pr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, pr.toJsonString());
    }

}
