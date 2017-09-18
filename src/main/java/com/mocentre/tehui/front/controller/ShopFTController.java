package com.mocentre.tehui.front.controller;

import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.CategoryWithGoodsFTInstance;
import com.mocentre.tehui.frontend.model.GoodsListPageInfoFTInstance;
import com.mocentre.tehui.frontend.model.ShopHomInfoFTInstance;
import com.mocentre.tehui.frontend.service.ShopManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 店铺接口
 * Created by 王雪莹 on 2016/12/24.
 */
@Controller
@RequestMapping("/front/shop")
public class ShopFTController extends BaseController{
    @Autowired
    private ShopManageService shopManageService;


    /**
     *  商品上新
     * @param request
     * @param response
     */
    @RequestMapping(value = "getAllShowGoods.htm", method = RequestMethod.POST)
    public void getAllShowGoods(HttpServletRequest request, HttpServletResponse response){
        String shopId = request.getParameter("shopId");
        ListResult<GoodsListPageInfoFTInstance> result = new ListResult<>();
        try {
            result = shopManageService.getAllShowGoods(Long.parseLong(shopId), generageRequestId());
        }catch (Exception e){
            result.setErrorMessage("999","系统错误");
        }
        super.printJson(response,result.toJsonString());
    }

    /**
     * 获取店铺首页
     * @param request
     * @param response
     */
    @RequestMapping(value = "getShopHomPageInfo.htm", method = RequestMethod.POST)
    public void getShopHomPageInfo(HttpServletRequest request, HttpServletResponse response){
        String shopId = request.getParameter("shopId");
        PlainResult<ShopHomInfoFTInstance> result = new PlainResult<>();
        try {
            result = shopManageService.getShopHomPageInfo(Long.parseLong(shopId), generageRequestId());
        }catch (Exception e){
            result.setErrorMessage("999","系统错误");
        }
        super.printJson(response,result.toJsonString());
    }
}
