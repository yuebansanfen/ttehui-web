/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.shop.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.backend.model.LoginUserInstance;
import com.mocentre.tehui.backend.model.ShopInstance;
import com.mocentre.tehui.backend.param.ShopParam;
import com.mocentre.tehui.backend.param.ShopQueryParam;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.shop.service.ShopService;
import com.mocentre.tehui.sys.service.UserService;

/**
 * 类ShopController.java的实现描述：对店铺进行操作controller
 *
 * @author YuKaiji 201611月13日
 */
@Controller
@RequestMapping("/sp/shop")
public class ShopController extends BaseController {

    @Autowired
    private ShopService shopService;
    @Autowired
    private UserService userService;

    /**
     * 商家登陆店铺详情页面
     *
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "uindex.htm", method = RequestMethod.GET)
    public String uindex(Model model, HttpServletRequest request, HttpServletResponse response) {
        Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
        if (shopId == null) {
            return getNameSpace() + "add";
        } else {
            ShopInstance shop = shopService.queryById(Long.valueOf(shopId.toString()));
            if (shop == null) {
                model.addAttribute("msg", "店铺不存在");
                return getErrorIndex();
            } else {
                model.addAttribute("shop", shop);
                return getNameSpace() + "uindex";
            }
        }
    }

    /**
     * 超级管理员店铺列表页面
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response) {
        return getNameSpace() + "index";
    }

    /**
     * 用户添加基本信息页面
     *
     * @return
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.GET)
    public String add() {
        return getNameSpace() + "add";
    }

    /**
     * 跳转到店铺审核页面
     *
     * @param id
     * @param model
     */
    @RequestMapping(value = "examine.htm", method = RequestMethod.GET)
    public String examine(Long id, Model model) {
        try {
            ShopInstance shop = shopService.queryById(id);
            if (shop == null) {
                model.addAttribute("msg", "店铺不存在");
                return getErrorIndex();
            } else {
                model.addAttribute("shop", shop);
            }
        } catch (Exception e) {
            LOGGER.error("examine", e);
        }
        return getNameSpace() + "examine";
    }

    /**
     * 用户修改基本信息页面
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "edit.htm", method = RequestMethod.GET)
    public String edit(Model model, HttpServletRequest request) {
        Object shop_id = request.getSession().getAttribute(SessionKeyConstant.SHOP);
        ShopInstance shop = shopService.queryById(Long.valueOf(shop_id.toString()));
        if (shop == null) {
            model.addAttribute("msg", "店铺不存在");
            return getErrorIndex();
        } else {
            model.addAttribute("shop", shop);
        }
        return getNameSpace() + "edit";
    }

    /**
     * 超级管理员查询所有店铺信息
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "query.htm", method = RequestMethod.POST)
    public void query(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<ShopInstance> br = new ListJsonResult<ShopInstance>();
        try {
            ShopQueryParam shopParam = bindClass(request, ShopQueryParam.class);
            br = shopService.queryShopPage(shopParam);
            super.printJson(response, br.toJsonString());
        } catch (Exception e) {
            LOGGER.error("queryPaged", e);
        }
    }

    /**
     * 店家添加方法
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "addshop.htm", method = RequestMethod.POST)
    public void addShop(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        Object user = request.getSession().getAttribute(SessionKeyConstant.USER);
        if (user == null) {
            br.setErrorMessage("1001", "用户id为空");
        }
        try {
            if (br.isSuccess()) {
                LoginUserInstance userIns = (LoginUserInstance) user;
                CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession()
                        .getServletContext());
                MultipartHttpServletRequest multiRequest = multipartResolver.resolveMultipart(request);
                ShopParam shopParam = bindClass(multiRequest, ShopParam.class);
                shopParam.setUserId(userIns.getId());
                PlainResult<Long> pr = shopService.useradd(shopParam);
                if (pr.isSuccess()) {
                    request.getSession().setAttribute(SessionKeyConstant.SHOP, pr.getData());
                } else {
                    br = pr;
                }
            }
        } catch (Exception e) {
            LOGGER.error("editShop", e);
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 店家修改方法
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "editshop.htm", method = RequestMethod.POST)
    public void editShop(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        try {
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession()
                    .getServletContext());
            MultipartHttpServletRequest multiRequest = multipartResolver.resolveMultipart(request);
            Object shopid = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            if (shopid != null) {
                ShopParam shopParam = bindClass(multiRequest, ShopParam.class);
                shopParam.setId((Long) shopid);
                br = shopService.edit(shopParam);
            } else {
                br.setErrorMessage("100", "修改失败");
            }
        } catch (Exception e) {
            LOGGER.error("editShop", e);
            e.printStackTrace();
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 根据id删除店铺
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    public void deleteShop(HttpServletRequest request, HttpServletResponse response) {
        String idList = request.getParameter("idList");
        shopService.delete(idList);
        super.printJson(response, "true");
    }

    /**
     * 操作一个店铺（开启、关闭、暂停）
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "operation.htm", method = RequestMethod.POST)
    public void operationShop(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String status = request.getParameter("status");
        try {
            shopService.operation(Long.valueOf(id), status);
        } catch (Exception e) {
            LOGGER.error("operation", e);
        }
        super.printJson(response, "true");
    }

    /**
     * 审核一个店铺（审核通过\驳回）
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "examineShop.htm", method = RequestMethod.POST)
    public void examineShop(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String status = request.getParameter("status");
        try {
            shopService.examine(Long.valueOf(id), status);
        } catch (Exception e) {
            LOGGER.error("examineShop", e);
        }
        super.printJson(response, "true");
    }

}
