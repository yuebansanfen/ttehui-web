package com.mocentre.gift.front.controller;

import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.gift.frontend.model.GiftGoodsFTInstance;
import com.mocentre.gift.frontend.param.GiftGoodsParam;
import com.mocentre.gift.frontend.param.GiftGoodsQueryParam;
import com.mocentre.gift.frontend.service.GiftGoodsManageService;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 礼品平台礼品controller
 * <p>
 * Created by yukaiji on 2017/04/09.
 */
@Controller
@RequestMapping("/giftFront/giftGoods")
public class GiftGoodsFTController extends BaseController {

    @Autowired
    private GiftGoodsManageService giftGoodsManageService;

    /**
     * 分页查询礼品列表
     */
    @RequestMapping(value = "getGoodsList.htm", method = RequestMethod.POST)
    public void getGoodsList(HttpServletRequest request, HttpServletResponse response) {
        ListResult<GiftGoodsFTInstance> lr = new ListResult<GiftGoodsFTInstance>();
        GiftGoodsQueryParam goodsQueryParam = bindClass(request, GiftGoodsQueryParam.class);
        if (goodsQueryParam == null) {
            lr.setErrorMessage("1001", "参数错误");
        }
        try {
            if (lr.isSuccess()) {
                lr = giftGoodsManageService.queryGiftGoodsList(goodsQueryParam);
            }
        } catch (Exception e) {
            LOGGER.error("getGoodsList.htm", e);
        }
        super.printJson(response, lr.toJsonString());
    }

    /**
     * 获取礼品详情
     */
    @RequestMapping(value = "getGoods.htm", method = RequestMethod.POST)
    public void getGoods(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<GiftGoodsFTInstance> pr = new PlainResult<GiftGoodsFTInstance>();
        GiftGoodsParam giftGoodsParam = bindClass(request, GiftGoodsParam.class);
        if (giftGoodsParam == null) {
            pr.setErrorMessage("1001", "参数错误");
        }
        try {
            if (pr.isSuccess()) {
                pr = giftGoodsManageService.getGiftGoodsById(giftGoodsParam);
            }
        } catch (Exception e) {
            LOGGER.error("getGoods.htm", e);
        }
        super.printJson(response, pr.toJsonString());
    }

}
