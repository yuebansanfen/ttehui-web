/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.sys.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.backend.model.LoginUserInstance;
import com.mocentre.tehui.backend.model.RoleInstance;
import com.mocentre.tehui.backend.model.ShopInstance;
import com.mocentre.tehui.backend.model.UserInstance;
import com.mocentre.tehui.backend.param.UserParam;
import com.mocentre.tehui.backend.param.UserQueryParam;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.annotation.SysLog;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.shop.service.ShopService;
import com.mocentre.tehui.sys.service.RoleService;
import com.mocentre.tehui.sys.service.UserService;

/**
 * 类UserController.java的实现描述：用户控制器
 *
 * @author sz.gong 2016年11月7日 下午1:51:02
 */
@Controller
@RequestMapping("/sys/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ShopService shopService;

    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response) {

        return getNameSpace() + "index";
    }

    @RequestMapping(value = "center.htm", method = RequestMethod.GET)
    public String center(HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            LoginUserInstance user = (LoginUserInstance) request.getSession().getAttribute(SessionKeyConstant.USER);
            PlainResult<UserInstance> result = userService.preEdit(user.getId());
            model.addAttribute("user", result.getData());
        } catch (Exception e) {
            LOGGER.error("center", e);
        }

        return getNameSpace() + "center";
    }

    @RequestMapping(value = "queryPaged.htm", method = RequestMethod.POST)
    public void queryPaged(HttpServletRequest request, HttpServletResponse response) {

        try {
            UserQueryParam userQueryParam = bindDTParamClass(request, UserQueryParam.class);
            ListJsonResult<UserInstance> pages = userService.queryPage(userQueryParam);
            super.printJson(response, pages.toJsonString());
        } catch (Exception e) {
            LOGGER.error("queryPaged", e);
        }
    }

    @RequestMapping(value = "add.htm")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) {
        List<RoleInstance> roleList = roleService.queryRole();
        model.addAttribute("roleList", roleList);
        return getNameSpace() + "add";
    }

    @RequestMapping(value = "edit.htm")
    public String edit(Long id, Model model) {

        try {
            PlainResult<UserInstance> prUser = userService.preEdit(id);
            if (prUser != null && prUser.isSuccess()) {
                UserInstance userIns = prUser.getData();
                List<Long> rids = userIns.getRoles();
                String roles = "";
                for (int i = 0; i < rids.size(); i++) {
                    Long rid = rids.get(i);
                    roles += rid + ",";
                }
                model.addAttribute("roles", roles);
                model.addAttribute("user", userIns);
                model.addAttribute("roleList", userIns.getRoleInsList());
            }
        } catch (Exception e) {
            LOGGER.error("edit", e);
        }
        return getNameSpace() + "edit";
    }

    @RequestMapping(value = "updateBase.htm", method = RequestMethod.POST)
    public void updateBase(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        try {
            UserParam user = bindClass(request, UserParam.class);
            Long id = user.getId();
            if (id == null) {
                br.setErrorMessage("100", "id为空");
            }
            if (br.isSuccess()) {
                br = userService.updaterBase(user);
            }
        } catch (Exception e) {
            LOGGER.error("updateBase", e);
        }
        printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "save.htm", method = RequestMethod.POST)
    @SysLog(title = "新增用户、编辑用户", value = "/sys/user/save", description = "新增用户、编辑用户")
    public void save(HttpServletRequest request, HttpServletResponse response, RedirectAttributes attr) {

        BaseResult br = new BaseResult();
        String roleIds = request.getParameter("roleIds");
        try {
            UserParam user = bindClass(request, UserParam.class);
            Long id = user.getId();
            String userName = user.getUserName();
            String password = user.getPassword();
            if (StringUtils.isBlank(userName)) {
                br.setErrorMessage("100", "用户名不能为空");
            }
            if (id == null && StringUtils.isBlank(password)) {
                br.setErrorMessage("100", "密码不能为空");
            }
            if (br.isSuccess()) {
                br = userService.saveOrUpdate(user, roleIds);
            }
        } catch (Exception e) {
            LOGGER.error("save", e);
        }
        printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    @SysLog(title = "删除用户", value = "/sys/user/delete.htm", description = "删除用户")
    public void delete(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        try {
            String ids = request.getParameter("ids");
            List<Long> idList = new ArrayList<Long>();
            for (String id : ids.split(",")) {
                idList.add(Long.parseLong(id));
            }
            br = userService.deleteById(idList);
        } catch (Exception e) {
            LOGGER.error("delete", e);
            br.setErrorMessage("99", "删除失败");
        }
        printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "shop_detail.htm", method = RequestMethod.GET)
    public String shopDetail(HttpServletRequest request, Model model) {

        try {
            String shopId = request.getParameter("shopId");
            if (StringUtils.isBlank(shopId)) {
                model.addAttribute("errorMsg", "店铺不存在");
                return getErrorIndex();
            }
            ShopInstance shop = shopService.queryById(Long.parseLong(shopId));
            model.addAttribute("shop", shop);
        } catch (Exception e) {
            LOGGER.error("shopDetail", e);
        }
        return getNameSpace() + "shop";
    }

}
