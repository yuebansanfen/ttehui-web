package com.mocentre.gift.front.controller;

import com.mocentre.common.ListResult;
import com.mocentre.gift.frontend.model.GiftBannerFTInstance;
import com.mocentre.gift.frontend.service.GiftBannerManageService;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 礼品平台前端banner接口调用Controller
 * <p>
 * Created by yukaiji on 2017/04/13.
 */
@Controller
@RequestMapping("/giftFront/mall")
public class GiftBannerFTController extends BaseController {

    @Autowired
    private GiftBannerManageService giftBannerManageService;

    /**
     * 查询展示banner列表
     */
    @RequestMapping(value = "queryBannerList.htm", method = RequestMethod.POST)
    public void queryGiftCategory(HttpServletRequest request, HttpServletResponse response) {
        ListResult<GiftBannerFTInstance> lr = new ListResult<GiftBannerFTInstance>();
        try {
            if (lr.isSuccess()) {
                lr = giftBannerManageService.queryBannerList(generageRequestId());
            }
        } catch (Exception e) {
            lr.setErrorMessage("999", "系统错误");
            LOGGER.error("queryBannerList.htm", e);
        }
        super.printJson(response, lr.toJsonString());
    }

}
