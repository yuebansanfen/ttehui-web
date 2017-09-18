package com.mocentre.tehui.wechat.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.ListResult;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.AreasFTInstance;
import com.mocentre.tehui.frontend.service.AreasManageService;

/**
 * 微信活动接口 Created by yukaiji on 2016/01/22.
 */

@Controller
@RequestMapping("/wxa/areas")
public class AreasWXController extends BaseController {

    @Autowired
    private AreasManageService areasManageService;

    @RequestMapping(value = "getAreas.htm", method = RequestMethod.GET)
    public void getAreas(HttpServletRequest request, HttpServletResponse response) {
        ListResult<AreasFTInstance> result = new ListResult<AreasFTInstance>();
        try {
            result = areasManageService.getAllProvinceThreeLinkage(generageRequestId());
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

}
