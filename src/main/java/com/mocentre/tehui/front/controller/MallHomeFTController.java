/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
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
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.MallAllFTInstance;
import com.mocentre.tehui.frontend.model.MallBannerWithActFTInstance;
import com.mocentre.tehui.frontend.model.MallHomeFTInstance;
import com.mocentre.tehui.frontend.param.SpecialSellQueryParam;
import com.mocentre.tehui.frontend.service.MallHomeManageService;

/**
 * 类MallHomeFTController.java的实现描述：客户端商城首页
 *
 * @author sz.gong 2017年5月19日 上午11:53:35
 */
@Controller
@RequestMapping("/front/mallHome")
public class MallHomeFTController extends BaseController {

    @Autowired
    private MallHomeManageService mallHomeManageService;

    @RequestMapping(value = "special_page.htm", method = RequestMethod.POST)
    public void specialPage(HttpServletRequest request, HttpServletResponse response) {

        BaseResult result = new ListResult<MallHomeFTInstance>();
        try {
            SpecialSellQueryParam sellParam = super.bindClass(request, SpecialSellQueryParam.class);
            sellParam.setRequestId(generageRequestId());
            result = mallHomeManageService.getSpecialSellPage(sellParam);
        } catch (Exception e) {
            LOGGER.error("specialPage", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    @RequestMapping(value = "queryBannerWithActivity.htm", method = RequestMethod.POST)
    public void queryBannerWithActivity(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<MallBannerWithActFTInstance> result = new PlainResult<MallBannerWithActFTInstance>();
        try {
            result = mallHomeManageService.getBannerWithActivity(generageRequestId());
        } catch (Exception e) {
            LOGGER.error("bannerWithActPage", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    @RequestMapping(value = "getList.htm", method = RequestMethod.POST)
    public void mallHomeList(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String flag = request.getParameter("flag");
        try {
            if (StringUtils.isNotBlank(flag) && "1qazxsw2".equals(flag)) {
                PlainResult<MallAllFTInstance> pr = mallHomeManageService.getMallHomeAll();
                if (pr.isSuccess()) {
                    //                    String dataPath = SystemConfig.INSTANCE.getValue(ConfigConstant.ABC_MALLHOME_DATA);
                    //                    String filePath = dataPath + File.separator + "mallHome.json";
                    //                    super.writeToJson(filePath, pr.toJsonString());
                }
            }
        } catch (Exception e) {
            LOGGER.error("getList", e);
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }
}
