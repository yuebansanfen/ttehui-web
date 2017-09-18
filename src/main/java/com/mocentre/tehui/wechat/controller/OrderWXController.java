/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.wechat.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.ActCashOrderFTInstance;
import com.mocentre.tehui.frontend.model.CustomerInfoFTInstance;
import com.mocentre.tehui.frontend.model.OrderDetailFTInstance;
import com.mocentre.tehui.frontend.model.OrderFTInstance;
import com.mocentre.tehui.frontend.model.WxaPayDataFTInstance;
import com.mocentre.tehui.frontend.param.ActCashGoodsParam;
import com.mocentre.tehui.frontend.param.ActOrderPayParam;
import com.mocentre.tehui.frontend.param.ActPayOrderParam;
import com.mocentre.tehui.frontend.param.CashierParam;
import com.mocentre.tehui.frontend.param.OrderQueryParam;
import com.mocentre.tehui.frontend.service.OrderManageService;
import com.mocentre.tehui.wechat.service.ToolUtil;

/**
 * 类OrderWXController.java的实现描述：订单controller
 * 
 * @author sz.gong 2017年2月12日 下午9:51:57
 */
@Controller
@RequestMapping("/wxa/order")
public class OrderWXController extends BaseController {

    @Autowired
    private OrderManageService orderMagService;
    @Autowired
    private RedisCache         redisCache;

    //    @RequestMapping(value = "test.htm", method = RequestMethod.GET)
    //    public void test(HttpServletRequest request, HttpServletResponse response) {
    //        String jsCode = request.getParameter("jsCode");
    //        PlainResult<WxaPayDataFTInstance> result = new PlainResult<WxaPayDataFTInstance>();
    //        try {
    //            ToolUtil tool = new ToolUtil();
    //            String openid = tool.getOpenid(jsCode);
    //            String ip = CommUtil.getIpAddr(request);
    //            if (StringUtils.isNotBlank(openid)) {
    //                result = orderMagService.testWx(openid, ip);
    //            } else {
    //                result.setErrorMessage("1000", "微信账号未绑定");
    //            }
    //        } catch (Exception e) {
    //            result.setErrorMessage("999", "系统错误");
    //        }
    //        super.printJson(response, result.toJsonString());
    //    }
    //

    /**
     * 立即购买
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "buy_now.htm", method = RequestMethod.POST)
    public void buyNow(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<ActCashOrderFTInstance> result = new PlainResult<ActCashOrderFTInstance>();
        try {
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            CashierParam param = bindClass(request, CashierParam.class);
            if (param.getGoodsId() == null || param.getBuyNum() == null || param.getGoodsSku() == null
                    || param.getActGoodsId() == null) {
                result.setErrorMessage("100", "参数错误");
            }
            if (result.isSuccess()) {
                param.setCustomerId(userInfo.getId());
                param.setRequestId(generageRequestId());
                result = orderMagService.actSubmitToCashier(param);
            }
        } catch (Exception e) {
            LOGGER.error("buyNow", e);
            result.setErrorMessage("99", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 提交订单
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "pay_order.htm", method = RequestMethod.POST)
    public void payOrder(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<WxaPayDataFTInstance> result = new PlainResult<WxaPayDataFTInstance>();
        try {
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            String cashGoodsJson = request.getParameter("goods");
            String jsCode = request.getParameter("jsCode");
            ActPayOrderParam param = bindClass(request, ActPayOrderParam.class);
            if (StringUtils.isBlank(cashGoodsJson) || param.getAddressId() == null || param.getActGoodsId() == null) {
                result.setErrorMessage("100", "参数错误");
            }
            String openid = userInfo.getOpenid();
            if (StringUtils.isBlank(openid)) {
                ToolUtil tool = new ToolUtil();
                openid = tool.getOpenid(jsCode);
            }
            if (StringUtils.isBlank(openid)) {
                result.setErrorMessage("101", "未绑定微信，支付失败");
            }
            if (result.isSuccess()) {
                ActCashGoodsParam cashGoods = JSON.toJavaObject(JSON.parseObject(cashGoodsJson),
                        ActCashGoodsParam.class);
                param.setCustomerId(userInfo.getId());
                param.setProfile(userInfo.getProfile());
                param.setRequestId(generageRequestId());
                param.setCashGoods(cashGoods);
                param.setIp(CommUtil.getIpAddr(request));
                param.setOpenid(openid);
                result = orderMagService.actSubmitOrder(param);
            }
        } catch (Exception e) {
            LOGGER.error("payOrder", e);
            result.setErrorMessage("99", "系统异常");
        }
        super.printJson(response, result.toJsonString());
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
            OrderQueryParam orderParam = bindClass(request, OrderQueryParam.class);
            if (StringUtils.isBlank(orderParam.getType())) {
                result.setErrorMessage("100", "参数错误");
            }
            if (result.isSuccess()) {
                orderParam.setCustomerId(userInfo.getId());
                orderParam.setRequestId(generageRequestId());
                result = orderMagService.orderList(orderParam);
            }
        } catch (Exception e) {
            LOGGER.error("orderList", e);
            result.setErrorMessage("99", "系统异常");
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
            String orderNum = request.getParameter("orderNum");
            if (StringUtils.isBlank(orderNum)) {
                result.setErrorMessage("100", "参数错误");
            }
            if (result.isSuccess()) {
                Long customerId = userInfo.getId();
                result = orderMagService.orderDetail(customerId, orderNum, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("orderDetail", e);
            result.setErrorMessage("99", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 未支付订单重新支付
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "order_pay.htm", method = RequestMethod.POST)
    public void orderPay(HttpServletRequest request, HttpServletResponse response) {

        BaseResult result = new PlainResult<WxaPayDataFTInstance>();
        try {
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            String jsCode = request.getParameter("jsCode");
            ActOrderPayParam payParam = super.bindClass(request, ActOrderPayParam.class);
            String orderNum = payParam.getOrderNum();
            if (StringUtils.isBlank(orderNum)) {
                result.setErrorMessage("100", "参数错误");
            }
            String openid = userInfo.getOpenid();
            if (StringUtils.isBlank(openid)) {
                ToolUtil tool = new ToolUtil();
                openid = tool.getOpenid(jsCode);
            }
            if (StringUtils.isBlank(openid)) {
                result.setErrorMessage("101", "未绑定微信，支付失败");
            }
            if (result.isSuccess()) {
                Long customerId = userInfo.getId();
                payParam.setCustomerId(customerId);
                payParam.setRequestId(generageRequestId());
                payParam.setIp(CommUtil.getIpAddr(request));
                payParam.setOpenid(openid);
                result = orderMagService.actOrderPay(payParam);
            }
        } catch (Exception e) {
            LOGGER.error("orderPay", e);
            result.setErrorMessage("99", "系统异常");
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
                result.setErrorMessage("101", "订单编号不能为空");
            }
            if (result.isSuccess()) {
                CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
                Long customerId = userInfo.getId();
                result = orderMagService.orderConfirm(orderNum, customerId, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("orderConfirm", e);
            result.setErrorMessage("99", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

}
