/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.buy.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

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
import com.mocentre.tehui.backend.model.LogisticsInstance;
import com.mocentre.tehui.backend.model.OrderDetailInstance;
import com.mocentre.tehui.backend.model.OrderInstance;
import com.mocentre.tehui.backend.param.LogisticsParam;
import com.mocentre.tehui.backend.param.OrderQueryParam;
import com.mocentre.tehui.buy.service.OrderService;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.common.util.SMSUtil;
import com.mocentre.tehui.core.controller.BaseController;

/**
 * 类OrderController.java的实现描述：订单控制器
 *
 * @author sz.gong 2016年11月11日 下午2:29:47
 */
@Controller
@RequestMapping("/buy/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response) {

        return getNameSpace() + "index";
    }

    @RequestMapping(value = "queryPaged.htm", method = RequestMethod.POST)
    public void queryPaged(HttpServletRequest request, HttpServletResponse response) {

        ListJsonResult<OrderInstance> br = new ListJsonResult<OrderInstance>();
        try {
            Long shopId = (Long) request.getSession().getAttribute(SessionKeyConstant.SHOP);
            if (shopId == null) {
                br.setErrorMessage("1001", "暂无店铺");
            }
            if (br.isSuccess()) {
                OrderQueryParam orderQueryParam = bindDTParamClass(request, OrderQueryParam.class);
                br = orderService.queryOrderPage(orderQueryParam, shopId);
            }
        } catch (Exception e) {
            LOGGER.error("queryPaged", e);
        }
        super.printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "detail.htm", method = RequestMethod.GET)
    public String detail(Long id, Model model, HttpServletRequest request) {

        try {
            Long shopId = (Long) request.getSession().getAttribute(SessionKeyConstant.SHOP);
            if (shopId == null) {
                return getErrorIndex();
            }
            PlainResult<Map<String, Object>> br = orderService.queryOrderDetail(id, shopId);
            if (!br.isSuccess()) {
                model.addAttribute("errorMsg", "查看详情失败");
                return getErrorIndex();
            }
            Map<String, Object> brMap = br.getData();
            OrderInstance order = (OrderInstance) brMap.get("order");
            List<OrderDetailInstance> detailList = (List<OrderDetailInstance>) brMap.get("detail");
            List<LogisticsInstance> logisticsList = (List<LogisticsInstance>) brMap.get("logistics");
            model.addAttribute("order", order);
            model.addAttribute("detailList", detailList);
            model.addAttribute("logisticsList", logisticsList);
        } catch (Exception e) {
            LOGGER.error("detail", e);
        }
        return getNameSpace() + "detail";
    }

    @RequestMapping(value = "send.htm", method = RequestMethod.POST)
    public void send(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        try {
            Long shopId = (Long) request.getSession().getAttribute(SessionKeyConstant.SHOP);
            LogisticsParam lgParam = bindClass(request, LogisticsParam.class);
            lgParam.setShopId(shopId);
            Long orderId = lgParam.getOrderId();
            if (orderId == null) {
                br.setErrorMessage("100", "提交失败");
                super.printJson(response, br.toJsonString());
                return;
            }
            PlainResult<String> pr = orderService.sendOrder(lgParam);
            if (pr.isSuccess()) {
                String telephone = pr.getData();
                String company = lgParam.getCompany();
                String expNum = lgParam.getExpNum();
                String message = "【天天特惠】尊敬的用户，您的包裹已经由" + company + "发出，运单号为" + expNum + "，请注意查收!";
                String res = SMSUtil.sendVerificationCode(telephone, message, true, generageRequestId());
                br = JSON.parseObject(res, BaseResult.class);
                if (!br.isSuccess()) {
                    br.setErrorMessage("1001", "验证码发送失败");
                }
            } else {
                br.setErrorMessage("1001", pr.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("send", e);
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "editSend.htm", method = RequestMethod.POST)
    public void editSend(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        try {
            Long shopId = (Long) request.getSession().getAttribute(SessionKeyConstant.SHOP);
            LogisticsParam lgParam = bindClass(request, LogisticsParam.class);
            lgParam.setShopId(shopId);
            Long orderId = lgParam.getOrderId();
            if (orderId == null) {
                br.setErrorMessage("1000", "提交失败");
            }
            if (br.isSuccess()) {
                br = orderService.editSendOrder(lgParam);
            }
        } catch (Exception e) {
            LOGGER.error("editSend", e);
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "orderRefund.htm", method = RequestMethod.POST)
    public void orderRefund(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        String orderNum = request.getParameter("orderNum");
        String orderDetailNum = request.getParameter("orderDetailNum");
        try {
            boolean paramValid = StringUtils.isBlank(orderNum) || StringUtils.isBlank(orderDetailNum);
            if (paramValid) {
                br.setErrorMessage("1000", "参数错误");
            }
            if (br.isSuccess()) {
                br = orderService.orderRefund(orderNum, orderDetailNum, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("orderRefund", e);
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "exportOrder.htm", method = RequestMethod.POST)
    public void exportOrder(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            Long shopId = (Long) request.getSession().getAttribute(SessionKeyConstant.SHOP);
            String beginTime = request.getParameter("beginTime");
            String endTime = request.getParameter("endTime");
            String payType = request.getParameter("payType");

            //获取服务器路径
            String dir = request.getSession().getServletContext().getRealPath("/");
            //获取文件路径
            if (StringUtils.isBlank(beginTime)) {
                br.setErrorMessage("1001", "请添加导出日期");
            }
            if (StringUtils.isBlank(endTime)) {
                br.setErrorMessage("1001", "请添加导出日期");
            }
            if (br.isSuccess()) {
                long now = System.currentTimeMillis();
                String userdir = dir + "orderFile/" + now + "/";
                String excelDir = orderService.uploadOrder(beginTime, endTime, payType, shopId, userdir);
                if (!StringUtils.isBlank(excelDir)) {
                    response.setContentType("application/ms-excel");
                    response.setHeader("Content-Disposition", "attachment;Filename=order" + now + ".xlsx");
                    OutputStream out;
                    try {
                        out = response.getOutputStream();
                        FileInputStream fis = new FileInputStream(excelDir);
                        // 写出流信息
                        int i = 0;
                        while ((i = fis.read()) != -1) {
                            out.write(i);
                        }
                        fis.close();
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        br.setErrorMessage("999", "下载失败");
                        LOGGER.error("exportOrder", e);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("exportOrder", e);
            e.printStackTrace();
        }
    }
}
