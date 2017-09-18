package com.mocentre.gift.front.controller;

import com.mocentre.common.BaseResult;
import com.mocentre.gift.frontend.param.GiftDemandParam;
import com.mocentre.gift.frontend.service.GiftDemandManageService;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 礼品平台极速获取controller
 * <p>
 * Created by yukaiji on 2017/04/09.
 */
@Controller
@RequestMapping("/giftFront/dma")
public class GiftDemandFTController extends BaseController {

    @Autowired
    private GiftDemandManageService giftDemandManageService;

    /**
     * 提交极速获取方案
     */
    @RequestMapping(value = "submitDemad.htm", method = RequestMethod.POST)
    public void submitDemad(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        GiftDemandParam giftDemandParam = this.bindClass(request, GiftDemandParam.class);
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        if (result.isSuccess()) {
            giftDemandParam.setRequestId(generageRequestId());
            result = giftDemandManageService.addGiftDemand(customerId, giftDemandParam);
        }
        super.printJson(response, result.toJsonString());
    }

}
