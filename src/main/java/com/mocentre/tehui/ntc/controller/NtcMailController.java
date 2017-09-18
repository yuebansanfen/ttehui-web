package com.mocentre.tehui.ntc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.NtcMailManageService;
import com.mocentre.tehui.backend.model.NtcMailInstance;
import com.mocentre.tehui.backend.param.NtcMailParam;
import com.mocentre.tehui.backend.param.NtcMailQueryParam;
import com.mocentre.tehui.core.controller.BaseController;

/**
 * 通知邮件controller Created by wangxueying on 2017/8/8.
 */
@Controller
@RequestMapping("/ntc/mail")
public class NtcMailController extends BaseController {
    @Autowired
    private NtcMailManageService ntcMailManageService;

    /**
     * 跳转到首页
     */
    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response) {
        return getNameSpace() + "index";
    }

    /**
     * 跳转到添加页面
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.GET)
    public String add(HttpServletRequest request, HttpServletResponse response) {
        return getNameSpace() + "add";
    }

    /**
     * 跳转到修改页面
     */
    @RequestMapping(value = "edit.htm", method = RequestMethod.GET)
    public String edit(Long id, Model model, HttpServletRequest request, HttpServletResponse response) {
        try {
            PlainResult<NtcMailInstance> pr = ntcMailManageService.getById(id, generageRequestId());
            if (pr.isSuccess()) {
                NtcMailInstance instance = pr.getData();
                model.addAttribute("ntcMail", instance);
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
        ListJsonResult<NtcMailInstance> br = new ListJsonResult<NtcMailInstance>();
        try {
            NtcMailQueryParam subjectQueryParam = bindClass(request, NtcMailQueryParam.class);
            br = ntcMailManageService.queryNtcMailPage(subjectQueryParam);
        } catch (Exception e) {
            LOGGER.error("query", e);
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 添加
     */
    @RequestMapping(value = "addNtcMail.htm", method = RequestMethod.POST)
    public void addNtcMail(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            NtcMailParam param = super.bindClass(request, NtcMailParam.class);
            param.setRequestId(generageRequestId());
            br = ntcMailManageService.addNtcMail(param);
        } catch (Exception e) {
            LOGGER.error("addNtcMail", e);
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 修改
     */
    @RequestMapping(value = "editNtcMail.htm", method = RequestMethod.POST)
    public void editNtcMail(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            NtcMailParam param = super.bindClass(request, NtcMailParam.class);
            param.setRequestId(generageRequestId());
            br = ntcMailManageService.updateNtcMail(param);
        } catch (Exception e) {
            LOGGER.error("editNtcMail", e);
            br.setErrorMessage("999", "系统错误");
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
        try {
            if (!StringUtils.isEmpty(id)) {
                br = ntcMailManageService.deleteNtcMail(Long.parseLong(id), generageRequestId());
            } else {
                br.setErrorMessage("1001", "参数错误");
            }

        } catch (Exception e) {
            LOGGER.error("delete", e);
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

}
