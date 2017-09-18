package com.mocentre.tehui.front.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.CashOrderFTInstance;
import com.mocentre.tehui.frontend.model.CustomerInfoFTInstance;
import com.mocentre.tehui.frontend.model.ShopCartDetailFTInstance;
import com.mocentre.tehui.frontend.model.ShopCartFTInstance;
import com.mocentre.tehui.frontend.model.ShopCartNumFTInstance;
import com.mocentre.tehui.frontend.param.ShopCartAddParam;
import com.mocentre.tehui.frontend.param.ShopCartChangeParam;
import com.mocentre.tehui.frontend.param.ShopCartDetailParam;
import com.mocentre.tehui.frontend.param.ShopCartListParam;
import com.mocentre.tehui.frontend.service.ShopCartManageService;

/**
 * 购物车接口 Created by 王雪莹 on 2016/12/24.
 */
@Controller
@RequestMapping("/front/shop_cart")
public class ShopCart2FTController extends BaseController {

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
        PlainResult<Integer> pr = new PlainResult<Integer>();
        ShopCartAddParam param = super.bindClass(request, ShopCartAddParam.class);
        param.setRequestId(generageRequestId());
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser(request);
        Long goodsId = param.getGoodsId();
        Long actGoodsId = param.getActGoodsId();
        String goodsSku = param.getGoodsSku();
        Integer num = param.getNum();
        boolean paramValid = goodsId == null || StringUtils.isBlank(goodsSku) || num == null;
        if (paramValid) {
            pr.setErrorMessage("1000", "参数错误");
        }
        try {
            List<ShopCartDetailFTInstance> scdFTInsList = null;
            HttpSession session = request.getSession();
            Object shopCartListObj = session.getAttribute(SessionKeyConstant.SHOPPING_CART_LIST);
            if (pr.isSuccess()) {
                if (userInfo != null) {//已登入
                    param.setCustomerId(userInfo.getId());
                } else {
                    if (shopCartListObj != null) {
                        scdFTInsList = (List<ShopCartDetailFTInstance>) shopCartListObj;
                        for (ShopCartDetailFTInstance scdFt : scdFTInsList) {
                            if (scdFt.getGoodsId().equals(goodsId) && scdFt.getActGoodsId().equals(actGoodsId)
                                    && scdFt.getGoodsSku().equals(goodsSku)) {
                                param.setNum(num + scdFt.getBuyNum());//购买的总数
                                break;
                            }
                        }
                    }
                }
                PlainResult<ShopCartNumFTInstance> result = shopCartManageService.add(param);
                if (result.isSuccess()) {
                    ShopCartNumFTInstance scnFTIns = result.getData();
                    Integer totalNum = scnFTIns.getTotalNum();
                    if (userInfo == null && scnFTIns != null) {
                        ShopCartDetailFTInstance scdFTIns = scnFTIns.getShopCartDetail();
                        if (shopCartListObj == null) {
                            scdFTInsList = new ArrayList<ShopCartDetailFTInstance>();
                            scdFTInsList.add(scdFTIns);
                        } else {
                            boolean isExist = false;
                            for (ShopCartDetailFTInstance scdFt : scdFTInsList) {
                                if (scdFt.getGoodsId().equals(scdFTIns.getGoodsId())
                                        && scdFt.getActGoodsId().equals(scdFTIns.getActGoodsId())
                                        && scdFt.getGoodsSku().equals(scdFTIns.getGoodsSku())) {
                                    scdFt.setBuyNum(scdFt.getBuyNum() + scdFTIns.getBuyNum());
                                    isExist = true;
                                    break;
                                }
                            }
                            if (!isExist) {
                                scdFTInsList.add(scdFTIns);
                            }
                        }
                        session.setAttribute(SessionKeyConstant.SHOPPING_CART_LIST, scdFTInsList);
                        pr.setData(scdFTInsList.size());
                    } else {
                        pr.setData(totalNum);
                    }
                } else {
                    pr.setErrorMessage(result.getCode(), result.getMessage());
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
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser(request);
        ListResult<ShopCartFTInstance> result = new ListResult<ShopCartFTInstance>();
        try {
            Long customerId = null;
            if (userInfo != null) {
                customerId = userInfo.getId();
            }
            if (result.isSuccess()) {
                List<ShopCartDetailParam> scdParamList = new ArrayList<ShopCartDetailParam>();
                if (userInfo == null) {
                    List<ShopCartDetailFTInstance> scdFTInsList = null;
                    Object shopCartListObj = request.getSession().getAttribute(SessionKeyConstant.SHOPPING_CART_LIST);
                    if (shopCartListObj != null) {
                        scdFTInsList = (List<ShopCartDetailFTInstance>) shopCartListObj;
                        for (ShopCartDetailFTInstance scdFTIns : scdFTInsList) {
                            ShopCartDetailParam scdParam = new ShopCartDetailParam();
                            scdParam.setActGoodsId(scdFTIns.getActGoodsId());
                            scdParam.setGoodsId(scdFTIns.getGoodsId());
                            scdParam.setGoodsSku(scdFTIns.getGoodsSku());
                            scdParam.setShopId(scdFTIns.getShopId());
                            scdParam.setShopName(scdFTIns.getShopName());
                            scdParam.setBuyNum(scdFTIns.getBuyNum());
                            scdParam.setGoodsSkuDes(scdFTIns.getGoodsSkuDes());
                            scdParam.setGoodsName(scdFTIns.getGoodsName());
                            scdParam.setShowLogo(scdFTIns.getShowLogo());
                            scdParam.setSellPrice(scdFTIns.getSellPrice());
                            scdParam.setOldPrice(scdFTIns.getOldPrice());
                            scdParamList.add(scdParam);
                        }
                    }
                }
                ShopCartListParam listParam = new ShopCartListParam();
                listParam.setCustomerId(customerId);
                listParam.setShopCartDetailParamList(scdParamList);
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
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser(request);
        ShopCartChangeParam changeParam = super.bindClass(request, ShopCartChangeParam.class);
        Long goodsId = changeParam.getGoodsId();
        Integer changeNum = changeParam.getChangeNum();
        String goodsSku = changeParam.getGoodsSku();
        Long actGoodsId = changeParam.getActGoodsId();
        boolean paramValid = goodsId == null || changeNum == null || changeNum == 0 || StringUtils.isBlank(goodsSku);
        if (paramValid) {
            result.setErrorMessage("1000", "参数错误");
        }
        try {
            if (userInfo != null) {
                changeParam.setCustomerId(userInfo.getId());
            }
            if (result.isSuccess()) {
                result = shopCartManageService.changeNum(changeParam);
                if (result.isSuccess() && userInfo == null) {//未登入
                    List<ShopCartDetailFTInstance> scdFTInsList = null;
                    Object shopCartListObj = request.getSession().getAttribute(SessionKeyConstant.SHOPPING_CART_LIST);
                    if (shopCartListObj != null) {
                        scdFTInsList = (List<ShopCartDetailFTInstance>) shopCartListObj;
                        for (int i = 0; i < scdFTInsList.size(); i++) {
                            ShopCartDetailFTInstance scdFTIns = scdFTInsList.get(i);
                            if (scdFTIns.getGoodsId().equals(goodsId) && scdFTIns.getGoodsSku().equals(goodsSku)
                                    && scdFTIns.getActGoodsId().equals(actGoodsId)) {
                                scdFTIns.setBuyNum(changeNum);
                                break;
                            }
                        }
                    }
                }
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
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser(request);
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
            if (result.isSuccess()) {
                if (userInfo == null) {
                    List<ShopCartDetailFTInstance> scdFTInsList = null;
                    Object shopCartListObj = request.getSession().getAttribute(SessionKeyConstant.SHOPPING_CART_LIST);
                    if (shopCartListObj != null) {
                        scdFTInsList = (List<ShopCartDetailFTInstance>) shopCartListObj;
                        for (int i = 0; i < scdFTInsList.size(); i++) {
                            ShopCartDetailFTInstance scdFTIns = scdFTInsList.get(i);
                            if (scdFTIns.getGoodsId().equals(goodsId) && scdFTIns.getGoodsSku().equals(goodsSku)
                                    && scdFTIns.getActGoodsId().equals(actGoodsId)) {
                                scdFTInsList.remove(i);
                                break;
                            }
                        }
                        request.getSession().setAttribute(SessionKeyConstant.SHOPPING_CART_LIST, scdFTInsList);
                    }
                } else {
                    changeParam.setCustomerId(userInfo.getId());
                    result = shopCartManageService.delete(changeParam);
                }
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
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser(request);
        PlainResult<CashOrderFTInstance> result = new PlainResult<CashOrderFTInstance>();
        try {
            ShopCartListParam listParam = new ShopCartListParam();
            listParam.setRequestId(generageRequestId());
            if (userInfo != null) {
                listParam.setCustomerId(userInfo.getId());
            } else {
                List<ShopCartDetailParam> scdParamList = new ArrayList<ShopCartDetailParam>();
                List<ShopCartDetailFTInstance> scdFTInsList = null;
                Object shopCartListObj = request.getSession().getAttribute(SessionKeyConstant.SHOPPING_CART_LIST);
                if (shopCartListObj != null) {
                    scdFTInsList = (List<ShopCartDetailFTInstance>) shopCartListObj;
                    for (ShopCartDetailFTInstance scdFTIns : scdFTInsList) {
                        ShopCartDetailParam scdParam = new ShopCartDetailParam();
                        scdParam.setActGoodsId(scdFTIns.getActGoodsId());
                        scdParam.setGoodsId(scdFTIns.getGoodsId());
                        scdParam.setGoodsSku(scdFTIns.getGoodsSku());
                        scdParam.setBuyNum(scdFTIns.getBuyNum());
                        scdParamList.add(scdParam);
                    }
                    listParam.setShopCartDetailParamList(scdParamList);
                }
            }
            if (result.isSuccess()) {
                result = shopCartManageService.buyCart(listParam);
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 购物车的数量
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "count.htm", method = RequestMethod.POST)
    public void count(HttpServletRequest request, HttpServletResponse response) {
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser(request);
        PlainResult<Integer> pr = new PlainResult<Integer>();
        try {
            if (userInfo != null && userInfo.getId() != null) {
                pr = shopCartManageService.getShopCartCount(userInfo.getId(), generageRequestId());
            } else {
                pr.setRequestId(generageRequestId());
                List<ShopCartDetailFTInstance> scdFTInsList = null;
                Object shopCartListObj = request.getSession().getAttribute(SessionKeyConstant.SHOPPING_CART_LIST);
                if (shopCartListObj != null) {
                    scdFTInsList = (List<ShopCartDetailFTInstance>) shopCartListObj;
                    pr.setData(scdFTInsList.size());
                } else {
                    pr.setData(0);
                }
            }
        } catch (Exception e) {
            pr.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, pr.toJsonString());
    }

}
