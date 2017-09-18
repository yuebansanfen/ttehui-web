/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.td.controller;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.ThirdGoodsManageService;
import com.mocentre.tehui.backend.model.ThirdGoodsInstance;
import com.mocentre.tehui.backend.param.ThirdGoodsParam;
import com.mocentre.tehui.backend.param.ThirdGoodsQueryParam;
import com.mocentre.tehui.core.controller.BaseController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 类ThirdGoodsController.java的实现描述：第三方商品
 * 
 * @author yukaiji 2017年7月12日
 */
@Controller
@RequestMapping("/td/tgoods")
public class ThirdGoodsController extends BaseController {

    @Autowired
    private ThirdGoodsManageService thirdGoodsManageService;

    /**
     * 跳转到首页
     */
    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index() {
        return getNameSpace() + "index";
    }

    /**
     * 跳转到添加页面
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.GET)
    public String add() {
        return getNameSpace() + "add";
    }

    /**
     * 跳转到修改页面
     */
    @RequestMapping(value = "edit.htm", method = RequestMethod.GET)
    public String edit(Long id, Model model, HttpServletRequest request) {
        try {
            PlainResult<ThirdGoodsInstance> result = thirdGoodsManageService.getThirdGoodsById(id, generageRequestId());
            if (result.isSuccess()) {
                model.addAttribute("thirdGoods", result.getData());
            } else {
                model.addAttribute("errorMsg", "访问失败");
                return getErrorIndex();
            }
        } catch (Exception e) {
            LOGGER.error("edit", e);
            model.addAttribute("errorMsg", "系统异常");
            return getErrorIndex();
        }
        return getNameSpace() + "edit";
    }

    /**
     * 分页查询
     */
    @RequestMapping(value = "query.htm", method = RequestMethod.POST)
    public void query(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<ThirdGoodsInstance> br = new ListJsonResult<ThirdGoodsInstance>();
        try {
            if (br.isSuccess()) {
                ThirdGoodsQueryParam queryParam = bindDTParamClass(request, ThirdGoodsQueryParam.class);
                br = thirdGoodsManageService.queryThirdGoodsPage(queryParam);
                super.printJson(response, br.toJsonString());
            }
        } catch (Exception e) {
            LOGGER.error("query", e);
        }
    }

    /**
     * 添加操作
     */
    @RequestMapping(value = "addThirdGoods.htm", method = RequestMethod.POST)
    public void addMallHome(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            ThirdGoodsParam thirdGoodsParam = super.bindClass(request, ThirdGoodsParam.class);
            br = thirdGoodsManageService.addThirdGoods(thirdGoodsParam);
        } catch (Exception e) {
            LOGGER.error("addThirdGoods", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 修改操作
     */
    @RequestMapping(value = "editThirdGoods.htm", method = RequestMethod.POST)
    public void editMallHome(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            ThirdGoodsParam thirdGoodsParam = super.bindClass(request, ThirdGoodsParam.class);
            br = thirdGoodsManageService.updateThirdGoods(thirdGoodsParam);
        } catch (Exception e) {
            LOGGER.error("editThirdGoods", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 删除
     */
    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        String showLocal = request.getParameter("showLocal");
        try {
            if (!StringUtils.isBlank(id)) {
                br = thirdGoodsManageService.delById(Long.valueOf(id), showLocal);
            } else {
                br.setErrorMessage("999", "删除失败");
            }

        } catch (Exception e) {
            LOGGER.error("delete", e);
        }
        super.printJson(response, br.toJsonString());
    }
}
