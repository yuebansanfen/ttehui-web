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
import com.mocentre.tehui.backend.model.RoleInstance;
import com.mocentre.tehui.backend.model.RuleInstance;
import com.mocentre.tehui.backend.param.RoleParam;
import com.mocentre.tehui.backend.param.RoleQueryParam;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.sys.service.RoleService;
import com.mocentre.tehui.sys.service.RuleService;

/**
 * 类RoleController.java的实现描述：角色控制器
 * 
 * @author sz.gong 2016年11月11日 下午5:40:15
 */
@Controller
@RequestMapping("/sys/role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private RuleService ruleService;

    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index() {
        return getNameSpace() + "index";
    }

    /**
     * 分页查询列表.
     */
    @RequestMapping(value = "queryPaged.htm", method = RequestMethod.POST)
    public void queryPaged(HttpServletRequest request, HttpServletResponse response) {

        ListJsonResult<RoleInstance> br = new ListJsonResult<RoleInstance>();
        try {
            RoleQueryParam roleParam = bindClass(request, RoleQueryParam.class);
            br = roleService.queryRolePage(roleParam);
        } catch (Exception e) {
            LOGGER.error("queryPaged", e);
        }
        super.printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "add.htm")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) {
        List<RuleInstance> ruleList = roleService.preAddRole();
        model.addAttribute("ruleList", ruleList);
        return getNameSpace() + "add";
    }

    @RequestMapping(value = "edit.htm")
    public String edit(Long id, Model model) {

        try {
            PlainResult<RoleInstance> result = roleService.preEditRole(id);
            if (result.isSuccess()) {
                RoleInstance roleIns = result.getData();
                List<RuleInstance> ruleList = roleIns.getRuleList();
                List<Long> ruleIds = roleIns.getRuleIds();
                model.addAttribute("ruleList", ruleList);
                model.addAttribute("role", roleIns);
                model.addAttribute("ckrule", ruleIds);
            } else {
                model.addAttribute("errorMsg", "访问异常");
                return getErrorIndex();
            }
        } catch (Exception e) {
            LOGGER.error("edit", e);
        }
        return getNameSpace() + "edit";
    }

    @RequestMapping(value = "save.htm", method = RequestMethod.POST)
    public void save(HttpServletRequest request, HttpServletResponse response, RedirectAttributes attr) {

        BaseResult br = new BaseResult();
        try {
            String ruleIds = request.getParameter("ruleIds");
            RoleParam roleParam = this.bindClass(request, RoleParam.class);
            br = roleService.saveRole(roleParam, ruleIds);
        } catch (Exception e) {
            LOGGER.error("save", e);
            br.setErrorMessage("99", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    public void delete(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        try {
            List<Long> idList = new ArrayList<Long>();
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",");
            for (int i = 0; i < idArr.length; i++) {
                idList.add(Long.parseLong(idArr[i]));
            }
            br = roleService.deleteRole(idList);
        } catch (Exception e) {
            LOGGER.error("delete", e);
            br.setErrorMessage("89", "系统异常");
        }
        printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "assign.htm")
    public String assign(HttpServletRequest request, HttpServletResponse response, Model model) {

        try {
            String id = request.getParameter("id");
            if (StringUtils.isBlank(id)) {
                model.addAttribute("errorMsg", "角色不存在");
                return getErrorIndex();
            }
            PlainResult<RoleInstance> br = roleService.preEditRole(Long.parseLong(id));
            if (br.isSuccess()) {
                RoleInstance roleIns = br.getData();
                List<RuleInstance> ruleList = roleIns.getRuleList();
                List<Long> ruleIds = roleIns.getRuleIds();
                model.addAttribute("ruleList", ruleList);
                model.addAttribute("ckrule", ruleIds);
                model.addAttribute("roleId", roleIns.getId());
            } else {
                model.addAttribute("errorMsg", "访问异常");
                return getErrorIndex();
            }
        } catch (Exception e) {
            LOGGER.error("assign", e);
        }
        return getNameSpace() + "assign";
    }

    @RequestMapping(value = "assignRule.htm", method = RequestMethod.POST)
    public void assignRule(HttpServletRequest request, HttpServletResponse response, RedirectAttributes attr) {

        BaseResult br = new BaseResult();
        try {
            String ruleIds = request.getParameter("ruleIds");
            String idStr = request.getParameter("id");
            if (StringUtils.isBlank(ruleIds) || StringUtils.isBlank(idStr)) {
                br.setErrorMessage("100", "参数错误");
            }
            if (br.isSuccess()) {
                br = roleService.assignRule(Long.parseLong(idStr), ruleIds);
            }
        } catch (Exception e) {
            LOGGER.error("save", e);
            br.setErrorMessage("99", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }

}
