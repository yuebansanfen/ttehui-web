/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.sys.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.ShareConfigManageService;
import com.mocentre.tehui.backend.model.ShareConfigInstance;
import com.mocentre.tehui.backend.param.ShareConfigParam;
import com.mocentre.tehui.backend.param.ShareConfigQueryParam;
import com.mocentre.tehui.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 微信分享配置 Created by yukaiji on 2017/05/4.
 */
@Controller
@RequestMapping("/sys/share")
public class ShareConfigController extends BaseController {

    @Autowired
    private ShareConfigManageService shareConfigManageService;

    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index() {
        return getNameSpace() + "index";
    }

    @RequestMapping(value = "add.htm", method = RequestMethod.GET)
    public String add() {
        return getNameSpace() + "add";
    }

    @RequestMapping(value = "edit.htm", method = RequestMethod.GET)
    public String edit(Model model, Long id) {
        PlainResult<ShareConfigInstance> result = new PlainResult<ShareConfigInstance>();
        if (id != null) {
            result = shareConfigManageService.getShareConfigById(id);
            model.addAttribute("shareConfig", result.getData());
        }
        return getNameSpace() + "edit";
    }

    @RequestMapping(value = "query.htm", method = RequestMethod.POST)
    public void query(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<ShareConfigInstance> br = new ListJsonResult<ShareConfigInstance>();
        try {
            ShareConfigQueryParam shareConfigQueryParam = bindClass(request, ShareConfigQueryParam.class);
            br = shareConfigManageService.queryShareConfigPage(shareConfigQueryParam);
            super.printJson(response, br.toJsonString());
        } catch (Exception e) {
            LOGGER.error("query", e);
        }
    }

    @RequestMapping(value = "addShareConfig.htm", method = RequestMethod.POST)
    public void addShareConfig(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            ShareConfigParam shareConfigParam = bindClass(request, ShareConfigParam.class);
            br = shareConfigManageService.addShareConfig(shareConfigParam);
        } catch (Exception e) {
            LOGGER.error("addShareConfig", e);
        }
        super.printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "editShareConfig.htm", method = RequestMethod.POST)
    public void editShareConfig(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            ShareConfigParam shareConfigParam = bindClass(request, ShareConfigParam.class);
            br = shareConfigManageService.updateShareConfig(shareConfigParam);
        } catch (Exception e) {
            LOGGER.error("editShareConfig", e);
        }
        super.printJson(response, br.toJsonString());
    }


    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        try {
            if (StringUtils.isBlank(id)) {
                br.setErrorMessage("1001", "id为空");
            }
            if (br.isSuccess()) {
                br = shareConfigManageService.delShareConfigById(Long.valueOf(id));
            }
        } catch (Exception e) {
            LOGGER.error("delete", e);
        }
        super.printJson(response, br.toJsonString());
    }
}
