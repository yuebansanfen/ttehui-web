/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.front.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.CashOrderFTInstance;
import com.mocentre.tehui.frontend.model.CustomerInfoFTInstance;
import com.mocentre.tehui.frontend.param.CashierParam;
import com.mocentre.tehui.frontend.service.OrderManageService;

/**
 * 类Order2FTController.java的实现描述：订单
 * 
 * @author sz.gong 2017年5月25日 上午10:41:02
 */
@Controller
@RequestMapping("/front/order_cash")
public class Order2FTController extends BaseController {

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
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser(request);
            CashierParam param = super.bindClass(request, CashierParam.class);
            boolean paramValid = StringUtils.isBlank(param.getGoodsSku()) || param.getBuyNum() == null;
            paramValid = param.getGoodsId() == null && param.getActGoodsId() == null;//商品和活动商品选其一
            if (paramValid) {
                result.setErrorMessage("1000", "参数错误");
            }
            if (userInfo != null) {
                //                LoggerUtil.tehuiwebLog.info("userInfo:" + userInfo.getId() + ":" + userInfo.getTelephone() + ":"
                //                        + userInfo.getAbcaid());
                param.setCustomerId(userInfo.getId());
            }
            if (result.isSuccess()) {
                result = orderMagService.submitToCashier(param);
            }
        } catch (Exception e) {
            LOGGER.error("buyNow", e);
        }
        super.printJson(response, result.toJsonString());
    }

}
