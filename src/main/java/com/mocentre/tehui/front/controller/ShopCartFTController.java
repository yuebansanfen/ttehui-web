package com.mocentre.tehui.front.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.CashOrderFTInstance;
import com.mocentre.tehui.frontend.model.CustomerInfoFTInstance;
import com.mocentre.tehui.frontend.model.ShopCartFTInstance;
import com.mocentre.tehui.frontend.model.ShopCartNumFTInstance;
import com.mocentre.tehui.frontend.param.ShopCartAddParam;
import com.mocentre.tehui.frontend.param.ShopCartChangeParam;
import com.mocentre.tehui.frontend.param.ShopCartListParam;
import com.mocentre.tehui.frontend.service.ShopCartManageService;

/**
 * 购物车接口 Created by 王雪莹 on 2016/12/24.
 */
@Controller
@RequestMapping("/front/shopCart")
public class ShopCartFTController extends BaseController {

    @Autowired
    private ShopCartManageService shopCartManageService;
    @Autowired
    private RedisCache            redisCache;

    /**
     * 添加到购物车
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.POST)
    public void add(HttpServletRequest request, HttpServletResponse response) {
        ShopCartAddParam param = super.bindClass(request, ShopCartAddParam.class);
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        PlainResult<Integer> pr = new PlainResult<Integer>();
        try {
            if (userInfo == null) {
                pr.setErrorMessage("1000", "用户不存在");
            }
            if (pr.isSuccess()) {
                param.setRequestId(generageRequestId());
                param.setCustomerId(userInfo.getId());
                PlainResult<ShopCartNumFTInstance> result = shopCartManageService.add(param);
                if (result.isSuccess()) {
                    ShopCartNumFTInstance scnFTIns = result.getData();
                    Integer totalNum = scnFTIns.getTotalNum();
                    pr.setData(totalNum);
                }
            }
        } catch (Exception e) {
            pr.setErrorMessage("999", "系统错误");
            LOGGER.error("add", e);
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 购物车列表
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "queryList.htm", method = RequestMethod.POST)
    public void queryList(HttpServletRequest request, HttpServletResponse response) {
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        ListResult<ShopCartFTInstance> result = new ListResult<ShopCartFTInstance>();
        try {
            if (userInfo == null) {
                result.setErrorMessage("1000", "用户不存在");
            }
            if (result.isSuccess()) {
                ShopCartListParam listParam = new ShopCartListParam();
                listParam.setCustomerId(userInfo.getId());
                listParam.setRequestId(generageRequestId());
                result = shopCartManageService.queryList(listParam);
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 更新指定购物车商品数量
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "changeNum.htm", method = RequestMethod.POST)
    public void changeNum(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        ShopCartChangeParam changeParam = super.bindClass(request, ShopCartChangeParam.class);
        Integer changeNum = changeParam.getChangeNum();
        Long goodsId = changeParam.getGoodsId();
        String goodsSku = changeParam.getGoodsSku();
        boolean paramValid = goodsId == null || changeNum == null || changeNum == 0 || StringUtils.isBlank(goodsSku);
        if (paramValid) {
            result.setErrorMessage("1000", "参数错误");
        }
        try {
            if (userInfo == null) {
                result.setErrorMessage("1000", "用户不存在");
            }
            if (result.isSuccess()) {
                changeParam.setCustomerId(userInfo.getId());
                result = shopCartManageService.changeNum(changeParam);
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 删除购物车商品
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        ShopCartChangeParam changeParam = super.bindClass(request, ShopCartChangeParam.class);
        Long goodsId = changeParam.getGoodsId();
        String goodsSku = changeParam.getGoodsSku();
        Long actGoodsId = changeParam.getActGoodsId();
        if (actGoodsId == null) {
            actGoodsId = 0l;
        }
        boolean paramValid = goodsId == null || StringUtils.isBlank(goodsSku);
        if (paramValid) {
            result.setErrorMessage("1000", "参数错误");
        }
        try {
            if (userInfo == null) {
                result.setErrorMessage("1000", "用户不存在");
            }
            if (result.isSuccess()) {
                changeParam.setCustomerId(userInfo.getId());
                result = shopCartManageService.delete(changeParam);
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 购物车提交订单
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "buy.htm", method = RequestMethod.POST)
    public void buy(HttpServletRequest request, HttpServletResponse response) {
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        PlainResult<CashOrderFTInstance> result = new PlainResult<CashOrderFTInstance>();
        try {
            if (userInfo == null) {
                result.setErrorMessage("1000", "用户不存在");
            }
            ShopCartListParam listParam = new ShopCartListParam();
            listParam.setRequestId(generageRequestId());
            if (result.isSuccess()) {
                listParam.setCustomerId(userInfo.getId());
                result = shopCartManageService.buyCart(listParam);
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 获取购物车商品数量
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "count.htm", method = RequestMethod.POST)
    public void getShopCartCount(HttpServletRequest request, HttpServletResponse response) {
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        PlainResult<Integer> result = new PlainResult<Integer>();
        try {
            if (userInfo == null) {
                result.setErrorMessage("1000", "用户不存在");
            }
            if (result.isSuccess()) {
                result = shopCartManageService.getShopCartCount(userInfo.getId(), generageRequestId());
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

}
