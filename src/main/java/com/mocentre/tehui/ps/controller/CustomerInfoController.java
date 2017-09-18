package com.mocentre.tehui.ps.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.CustomerInfoManageService;
import com.mocentre.tehui.backend.model.CustomerInfoInstance;
import com.mocentre.tehui.backend.param.CustomerInfoQueryParam;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.core.controller.BaseController;

/**
 * 用户基本信息 Created by 王雪莹 on 2016/11/23.
 */
@Controller
@RequestMapping("/ps/customerInfo")
public class CustomerInfoController extends BaseController {

    @Autowired
    private CustomerInfoManageService customerInfoManageService;

    /**
     * 跳转首页
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
     * 跳转详情页
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "info.htm", method = RequestMethod.GET)
    public String info(HttpServletRequest request, HttpServletResponse response, Model model) {
        String idStr = request.getParameter("id");
        PlainResult<CustomerInfoInstance> pr = new PlainResult<CustomerInfoInstance>();
        try {
            if (!StringUtils.isEmpty(idStr)) {
                Long idl = Long.parseLong(idStr);
                pr = customerInfoManageService.getById(idl, generageRequestId());
                if (pr.isSuccess()) {
                    CustomerInfoInstance cusInfo = pr.getData();
                    model.addAttribute("cusInfo", cusInfo);
                } else {
                    model.addAttribute("errorMsg", pr.getMessage());
                    return getErrorIndex();
                }
            } else {
                model.addAttribute("errorMsg", "参数错误");
                return getErrorIndex();
            }
        } catch (Exception e) {
            LOGGER.error("info", e);
        }
        return getNameSpace() + "info";
    }

    /**
     * 分页查询
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "queryPage.htm", method = RequestMethod.POST)
    public void queryPage(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<CustomerInfoInstance> result = new ListJsonResult<CustomerInfoInstance>();
        try {
            CustomerInfoQueryParam cumParam = super.bindDTParamClass(request, CustomerInfoQueryParam.class);
            result = customerInfoManageService.queryPage(cumParam);
        } catch (Exception e) {
            LOGGER.error("queryPage", e);
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 批量删除
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "delList.htm", method = RequestMethod.POST)
    public void delByIdList(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            String ids = request.getParameter("ids");
            if (StringUtils.isNotEmpty(ids)) {
                List<Long> idList = JSON.parseArray(ids, Long.class);
                br = customerInfoManageService.deleteCustomerByIdList(idList, CommUtil.generateUUID());
            }
        } catch (Exception e) {
            LOGGER.error("delete", e);
            br.setErrorMessage("98", "删除失败");
        }
        printJson(response, br.toJsonString());
    }

}
