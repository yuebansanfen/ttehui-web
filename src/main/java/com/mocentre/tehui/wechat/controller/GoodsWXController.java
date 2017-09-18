package com.mocentre.tehui.wechat.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.PlainResult;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.ActGoodsDetailFTInstance;
import com.mocentre.tehui.frontend.model.GoodsStorageAndPriceFTInstance;
import com.mocentre.tehui.frontend.service.GoodsManageService;

/**
 * 参与活动的商品详情 Created by 王雪莹 on 2017/2/14.
 */
@Controller
@RequestMapping("/wxa/goods")
public class GoodsWXController extends BaseController {
    @Autowired
    private GoodsManageService goodsManageService;

    @RequestMapping(value = "getActGoodsDetail.htm", method = RequestMethod.POST)
    public void getActGoodsDetail(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<ActGoodsDetailFTInstance> result = new PlainResult<>();
        try {
            String goodsId = request.getParameter("goodsId");
            String actGoodsId = request.getParameter("actGoodsId");
            String grouponIdStr = request.getParameter("grouponId");
            Long grouponId = null;
            if (StringUtils.isNotEmpty(grouponIdStr)) {
                grouponId = Long.parseLong(grouponIdStr);
            }
            result = goodsManageService.actGoodsDetailPage(Long.parseLong(goodsId), Long.parseLong(actGoodsId),
                    grouponId, generageRequestId());
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    @RequestMapping(value = "getActGoodsStorageAndPrice.htm", method = RequestMethod.POST)
    public void getActGoodsStorageAndPrice(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<GoodsStorageAndPriceFTInstance> result = new PlainResult<GoodsStorageAndPriceFTInstance>();
        try {
            String goodsId = request.getParameter("goodsId");
            String actGoodsId = request.getParameter("actGoodsId");
            String standardCode = request.getParameter("standardCode");
            result = goodsManageService.actGoodsStorageAndPrice(Long.parseLong(goodsId), Long.parseLong(actGoodsId),
                    standardCode, generageRequestId());
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    // 开团
}
