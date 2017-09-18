package com.mocentre.tehui.front.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.ListResult;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.NavigateFTInstance;
import com.mocentre.tehui.frontend.service.NavigateManageService;

/**
 * 导航栏 Created by 王雪莹 on 2016/12/23.
 */
@Controller
@RequestMapping("/front/navigate")
public class NavigateFTController extends BaseController {

    @Autowired
    private NavigateManageService navigateManageService;

    @RequestMapping(value = "getAll.htm", method = RequestMethod.POST)
    public void getAll(HttpServletRequest request, HttpServletResponse response) {
        ListResult<NavigateFTInstance> lr = new ListResult<NavigateFTInstance>();
        try {
            lr = navigateManageService.getAll(generageRequestId());
        } catch (Exception e) {
            LOGGER.error("getAll", e);
            lr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, lr.toJsonString());
    }

}
