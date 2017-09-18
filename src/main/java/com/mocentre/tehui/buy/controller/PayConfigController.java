package com.mocentre.tehui.buy.controller;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.tehui.PayConfigManageService;
import com.mocentre.tehui.backend.model.PayConfigInstance;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 支付管理
 *
 * Created by wangxueying on 2017/8/30.
 */
@Controller
@RequestMapping("/buy/payConfig")
public class PayConfigController extends BaseController {
    @Autowired
    private PayConfigManageService payConfigManageService;

    /**
     * 跳转到首页
     */
    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response) {
        return getNameSpace() + "index";
    }

    /**
     * 查询列表
     */
    @RequestMapping(value = "getAll.htm", method = RequestMethod.POST)
    public void getAll(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<PayConfigInstance> lr = new ListJsonResult<PayConfigInstance>();
        try {
            Long shopId = (Long) request.getSession().getAttribute(SessionKeyConstant.SHOP);
            if (shopId == null) {
                lr.setErrorMessage("1001", "暂无店铺");
            }
            lr = payConfigManageService.getAll(generageRequestId());
        } catch (Exception e) {
            LOGGER.error("getAll", e);
            lr.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, lr.toJsonString());
    }

    /**
     * 修改
     */
    @RequestMapping(value = "editOpenStatus.htm", method = RequestMethod.POST)
    public void editOpenStatus(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            String id = request.getParameter("id");
            String status = request.getParameter("status");
            boolean paramValid = StringUtils.isBlank(id) || StringUtils.isBlank(status);
            if (paramValid) {
                br.setErrorMessage("1000", "参数错误");
            }
            br = payConfigManageService.updateOpenStatus(Long.parseLong(id),status,generageRequestId());
        } catch (Exception e) {
            LOGGER.error("editOpenStatus", e);
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }
}
