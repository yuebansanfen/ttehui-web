/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.sys.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.PlainResult;
import com.mocentre.tehui.LoginManageService;
import com.mocentre.tehui.backend.model.LoginUserInstance;
import com.mocentre.tehui.backend.model.RuleInstance;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.core.utils.response.BaseResult;

/**
 * 类LoginController.java的实现描述：登入controller
 * 
 * @author sz.gong 2016年11月7日 下午4:04:07
 */
@Controller
@RequestMapping("/sys/login")
public class LoginController extends BaseController {

    @Autowired
    private LoginManageService loginMagService;

    @RequestMapping(value = "index")
    public String index(HttpServletRequest request, HttpServletResponse response) {
        return getNameSpace() + "index";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public void login(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String vcode = request.getParameter("code");
            if (StringUtils.isBlank(username)) {
                br.setErrorMessage("1000", "用户名不能为空");
            }
            if (StringUtils.isBlank(password)) {
                br.setErrorMessage("1000", "密码不能为空");
            }
            Object code = request.getSession().getAttribute(SessionKeyConstant.VERIFYCODE);
            String sescode = String.valueOf(code);
            if (!vcode.equalsIgnoreCase(sescode)) {
                br.setErrorMessage("1001", "验证码错误");
            }
            if (br.isSuccess()) {
                PlainResult<LoginUserInstance> loginUser = loginMagService.login(username, password,
                        generageRequestId());
                if (loginUser != null) {
                    if (loginUser.isSuccess()) {
                        LoginUserInstance userIns = loginUser.getData();
                        List<RuleInstance> ruleList = userIns.getRuleModelList();
                        userIns.setRuleModelList(null);
                        request.getSession().setAttribute(SessionKeyConstant.MENU, ruleList);
                        request.getSession().setAttribute(SessionKeyConstant.USER, userIns);
                        request.getSession().setAttribute(SessionKeyConstant.SHOP, userIns.getShopId());
                    } else {
                        br.setErrorMessage(loginUser.getCode(), loginUser.getMessage());
                    }
                } else {
                    br.setErrorMessage("1002", "接口异常");
                }
            }
        } catch (Exception e) {
            LOGGER.error("login", e);
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        session.removeAttribute(SessionKeyConstant.MENU);
        session.removeAttribute(SessionKeyConstant.USER);
        session.removeAttribute(SessionKeyConstant.SHOP);
        session.invalidate();
        String path = request.getContextPath();
        String basePath = path + "/login.html";
        try {
            response.getWriter().write(
                    "<script type='text/javascript'>window.location.href='" + basePath + "';</script>");
        } catch (Exception e) {
            LOGGER.error("logout", e);
        }
    }

    @RequestMapping(value = "noPermission")
    public String noPermission(HttpServletRequest request, HttpServletResponse response) {

        try {

        } catch (Exception e) {
            LOGGER.error("noPermission", e);
        }
        return getNameSpace() + "permission";
    }

}
