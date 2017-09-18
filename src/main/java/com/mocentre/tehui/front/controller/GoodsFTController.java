package com.mocentre.tehui.front.controller;

import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.backend.param.CategoryGoodsQueryParam;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.*;
import com.mocentre.tehui.frontend.service.GoodsManageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 王雪莹 on 2016/12/23.
 */
@Controller
@RequestMapping("/front/goods")
public class GoodsFTController extends BaseController {

    @Autowired
    private GoodsManageService goodsManageService;
    @Autowired
    private RedisCache         redisCache;

    @RequestMapping(value = "getGoodsDetail.htm", method = RequestMethod.POST)
    public void getByGoodsId(HttpServletRequest request, HttpServletResponse response) {
        String goodsId = request.getParameter("goodsId");
        PlainResult<GoodsDetailFTInstance> result = new PlainResult<>();
        try {
            result = goodsManageService.goodsDetailPage(Long.parseLong(goodsId), generageRequestId());
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    @RequestMapping(value = "getGoodsStorageAndPrice.htm", method = RequestMethod.POST)
    public void getGoodsStorageAndPrice(HttpServletRequest request, HttpServletResponse response) {
        String goodsId = request.getParameter("goodsId");
        String standardCode = request.getParameter("standardCode");
        PlainResult<GoodsStorageAndPriceFTInstance> result = new PlainResult<>();
        try {
            result = goodsManageService
                    .goodsStorageAndPrice(Long.parseLong(goodsId), standardCode, generageRequestId());
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    @RequestMapping(value = "getGoodsWithStorageAndPrice.htm", method = RequestMethod.POST)
    public void getGoodsWithStorageAndPrice(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<GoodsDetailWithStorageAndPriceFTInstance> pr = new PlainResult<>();
        String goodsIdStr = request.getParameter("goodsId");
        String actGoodsIdStr = request.getParameter("actGoodsId");
        String comefrom = request.getParameter("comefrom");
        try {
            boolean paramValid = StringUtils.isBlank(goodsIdStr) && StringUtils.isBlank(actGoodsIdStr);
            if (paramValid) {
                pr.setErrorMessage("1000", "参数错误");
            }
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser(request);
            Long customerId = null;
            if (userInfo != null) {
                customerId = userInfo.getId();
            }
            Long goodsId = null;
            Long actGoodsId = null;
            if (StringUtils.isNotBlank(actGoodsIdStr)) {
                actGoodsId = Long.parseLong(actGoodsIdStr);
            }
            if (StringUtils.isNotBlank(goodsIdStr)) {
                goodsId = Long.parseLong(goodsIdStr);
            }
            if (pr.isSuccess()) {
                pr = goodsManageService.getGoodsWithStorageAndPrice(goodsId, customerId, actGoodsId, comefrom,
                        generageRequestId());
            }
        } catch (Exception e) {
            pr.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 根据店铺id获取所有分类及该分类下的商品
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "getAllCategoryWithGoods.htm", method = RequestMethod.POST)
    public void getAllCategoryWithGoods(HttpServletRequest request, HttpServletResponse response) {
        ListResult<CategoryWithGoodsFTInstance> result = new ListResult<>();
        CategoryGoodsQueryParam cgParam = super.bindClass(request, CategoryGoodsQueryParam.class);
        cgParam.setRequestId(generageRequestId());
        try {
            result = goodsManageService.getAllCategoryWithGoods(cgParam);
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 猜你喜欢
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "getGuessLike.htm", method = RequestMethod.POST)
    public void getGuessLike(HttpServletRequest request, HttpServletResponse response) {
        ListResult<GoodsListPageInfoFTInstance> result = new ListResult<>();

        String num = request.getParameter("num");
        try {

            if (StringUtils.isBlank(num)) {
                result.setErrorMessage("1001","参数错误");
            }
            if (result.isSuccess()){
                result = goodsManageService.getGuessLike(Integer.valueOf(num), generageRequestId());
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

}
