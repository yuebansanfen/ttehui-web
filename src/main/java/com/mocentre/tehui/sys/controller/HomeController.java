/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.sys.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.tehui.core.controller.BaseController;

/**
 * 类MenuController.java的实现描述：控制台
 * 
 * @author sz.gong 2016年11月7日 下午6:15:48
 */
@Controller
@RequestMapping("/sys/home")
public class HomeController extends BaseController {

    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String menu(Model model, HttpServletRequest request, HttpServletResponse response) {

        return getNameSpace() + "index";
    }
}
