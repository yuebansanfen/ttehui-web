package com.mocentre.tehui.front.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.ListResult;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.DiscoverFTInstance;
import com.mocentre.tehui.frontend.service.DiscoverManageService;

/**
 * 发现页 Created by 王雪莹 on 2016/12/23.
 */
@Controller
@RequestMapping("/front/discover")
public class DiscoverFTController extends BaseController {
    @Autowired
    private DiscoverManageService discovermanageService;

    @RequestMapping(value = "getByShopId.htm", method = RequestMethod.POST)
    public void getByShopId(HttpServletRequest request, HttpServletResponse response) {
        ListResult<DiscoverFTInstance> result = new ListResult<>();
        try {
            result = discovermanageService.getDiscoverList(generageRequestId());
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }
}
