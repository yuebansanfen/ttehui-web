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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListResult;
import com.mocentre.tehui.backend.model.RuleInstance;
import com.mocentre.tehui.backend.param.RuleParam;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.sys.service.RuleService;

/**
 * 类RuleController.java的实现描述：菜单控制器
 * 
 * @author sz.gong 2016年11月8日 上午10:24:22
 */
@Controller
@RequestMapping("/sys/rule")
public class RuleController extends BaseController {

    @Autowired
    private RuleService ruleService;

    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index() {
        return getNameSpace() + "index";
    }

    @RequestMapping(value = "queryList.htm", method = RequestMethod.POST)
    public void queryList(HttpServletRequest request, HttpServletResponse response) {

        ListResult<RuleInstance> br = new ListResult<RuleInstance>();
        try {
            Map<String, String> paramMap = bindMap(request);
            String title = paramMap.get("title");
            br = ruleService.queryList(title);
        } catch (Exception e) {
            LOGGER.error("queryList", e);
        }
        super.printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "add.htm")
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) {

        List<RuleInstance> list = new ArrayList<RuleInstance>();
        try {
            list = ruleService.queryRuleCascade();
        } catch (Exception e) {
            LOGGER.error("add", e);
        }
        model.addAttribute("ruleList", list);
        return getNameSpace() + "add";
    }

    @RequestMapping(value = "edit.htm")
    public String edit(Long id, Model model) {

        List<RuleInstance> ruleList = ruleService.queryRuleCascade();
        model.addAttribute("ruleList", ruleList);
        if (id != null) {
            RuleInstance rule = ruleService.queryRuleById(id);
            model.addAttribute("rule", rule);
        }
        return getNameSpace() + "edit";
    }

    @RequestMapping(value = "save.htm", method = RequestMethod.POST)
    //@SysLog(title = "新增菜单、编辑菜单", value = "/sys/rule/save", description = "新增菜单、编辑菜单")
    //@Token(remove = true)
    public void save(HttpServletRequest request, HttpServletResponse response, RedirectAttributes attr) {

        BaseResult br = new BaseResult();
        try {
            RuleParam rule = bindClass(request, RuleParam.class);
            Boolean suc = ruleService.saveRule(rule);
            if (!suc) {
                br.setErrorMessage("1000", "保存失败");
            }
        } catch (Exception e) {
            LOGGER.error("save", e);
            br.setErrorMessage("999", "系统异常");
        }
        printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    //@SysLog(title = "删除菜单", value = "/sys/rule/delete.htm", description = "删除菜单")
    public void delete(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        try {
            List<Long> idList = new ArrayList<Long>();
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",");
            for (int i = 0; i < idArr.length; i++) {
                idList.add(Long.parseLong(idArr[i]));
            }
            Boolean suc = ruleService.deleteRule(idList);
            if (!suc) {
                br.setErrorMessage("1000", "删除失败");
            }
        } catch (Exception e) {
            LOGGER.error("delete", e);
            br.setErrorMessage("999", "系统异常");
        }
        printJson(response, br.toJsonString());
    }

}
