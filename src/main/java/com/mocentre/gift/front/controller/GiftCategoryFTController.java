package com.mocentre.gift.front.controller;

import com.mocentre.common.ListResult;
import com.mocentre.gift.frontend.model.GiftCategoryFTInstance;
import com.mocentre.gift.frontend.param.GiftCategoreParam;
import com.mocentre.gift.frontend.service.GiftCategoryManageService;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 礼品平台分类controller
 * <p>
 * Created by yukaiji on 2017/04/09.
 */
@Controller
@RequestMapping("/giftFront/giftCategory")
public class GiftCategoryFTController extends BaseController {

    @Autowired
    private GiftCategoryManageService giftCategoryManageService;

    /**
     * 查询具体分类
     */
    @RequestMapping(value = "getCategory.htm", method = RequestMethod.POST)
    public void queryGiftCategory(HttpServletRequest request, HttpServletResponse response) {
        ListResult<GiftCategoryFTInstance> lr = new ListResult<GiftCategoryFTInstance>();
        GiftCategoreParam giftCategoreParam = bindClass(request, GiftCategoreParam.class);
        if (giftCategoreParam == null) {
            lr.setErrorMessage("1001", "参数错误");
        }
        try {
            if (lr.isSuccess()) {
                lr = giftCategoryManageService.queryGiftCategory(giftCategoreParam);
            }
        } catch (Exception e) {
            LOGGER.error("getCategore.htm", e);
        }
        super.printJson(response, lr.toJsonString());
    }

}
