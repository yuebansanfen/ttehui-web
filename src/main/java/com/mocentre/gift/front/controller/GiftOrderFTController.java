package com.mocentre.gift.front.controller;


import com.alibaba.fastjson.JSON;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.gift.frontend.model.GiftGoodsPageFTInstance;
import com.mocentre.gift.frontend.model.GiftOrderDetailFTInstance;
import com.mocentre.gift.frontend.model.GiftOrderPageFTInstance;
import com.mocentre.gift.frontend.param.GiftGiftSheetFTParam;
import com.mocentre.gift.frontend.param.GiftOrderQueryParam;
import com.mocentre.gift.frontend.service.GiftOrderManageService;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.common.util.ExcelUtil;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.core.utils.DateUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车相关接口
 * Created by 王雪莹 on 2017/4/15.
 */
@Controller
@RequestMapping("/giftFront/order")
public class GiftOrderFTController extends BaseController {

    @Autowired
    private GiftOrderManageService service;

    /**
     * 提交订单(并删除礼品单里相应的礼品)
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "commitOrder.htm", method = RequestMethod.POST)
    public void commitOrder(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<String> result = new PlainResult<String>();
        String goodsArrStr = request.getParameter("cashGoodsJsonArr");
        String note = request.getParameter("note");
        String billStr = request.getParameter("bill");
        String addressId = request.getParameter("addressId");

        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);

        if (goodsArrStr == null) {
            result.setErrorMessage("999", "参数错误");
        }
        if (result.isSuccess()) {
            List<GiftGiftSheetFTParam> giftSheetList = JSON.parseArray(goodsArrStr, GiftGiftSheetFTParam.class);
            result = service.commitOrder(customerId, giftSheetList, note, billStr, addressId, generageRequestId());
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 订单详情
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "order_detail.htm", method = RequestMethod.POST)
    public void orderDetail(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<GiftOrderDetailFTInstance> result = new PlainResult<>();
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        String orderNum = request.getParameter("orderNum");
        if (orderNum == null) {
            result.setErrorMessage("999", "参数错误");
        }
        try {
            result = service.orderDetail(customerId, orderNum, generageRequestId());
        } catch (Exception e) {
            LOGGER.error("orderDetail", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 订单详情
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "order_list.htm", method = RequestMethod.POST)
    public void orderList(HttpServletRequest request, HttpServletResponse response) {
        ListResult<GiftOrderPageFTInstance> result = new ListResult<>();
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        try {
            if (result.isSuccess()) {
                GiftOrderQueryParam orderParam = super.bindClass(request, GiftOrderQueryParam.class);
                orderParam.setCustomerId(customerId);
                result = service.orderList(orderParam);
            }
        } catch (Exception e) {
            LOGGER.error("orderList", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 撤回取消
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "backedCancel.htm", method = RequestMethod.POST)
    public void backedCancel(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<GiftOrderDetailFTInstance> result = new PlainResult<>();
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        String orderNum = request.getParameter("orderNum");
        if (orderNum == null) {
            result.setErrorMessage("999", "参数错误");
        }
        try {
            result = service.backedCancel(customerId, orderNum, generageRequestId());
        } catch (Exception e) {
            LOGGER.error("orderDetail", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 取消
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "cancel.htm", method = RequestMethod.POST)
    public void cancel(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<GiftOrderDetailFTInstance> result = new PlainResult<>();
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        String orderNum = request.getParameter("orderNum");
        if (orderNum == null) {
            result.setErrorMessage("999", "参数错误");
        }
        try {
            result = service.cancel(customerId, orderNum, generageRequestId());
        } catch (Exception e) {
            LOGGER.error("orderDetail", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }


    @RequestMapping(value = "uploadOrder.htm", method = RequestMethod.POST)
    public void exeUploadFile(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        String orderNum = request.getParameter("orderNum");
        //获取服务器路径
        String dir = request.getSession().getServletContext().getRealPath("/");
        //获取文件路径
        String userdir = dir + "orderFile/" + orderNum + "/";
        try {
            String filePath = null;
            // 标题
            String[] nameString = {"序号", "商品名称", "购买数量", "商品单价", "商品总价"};
            // 内容
            List<String[]> info = new ArrayList<>();
            PlainResult<GiftOrderDetailFTInstance> result = service.orderDetail(customerId, orderNum, generageRequestId());
            GiftOrderDetailFTInstance orderDetail = result.getData();

            // 订单基本信息
            Map<String, Object> tableMap = new HashMap<String, Object>();
            tableMap.put("订单编号:", orderDetail.getOrderNum());
            tableMap.put("订单时间:", DateUtils.formatTime(orderDetail.getOrderTime()));
            tableMap.put("订单金额:", orderDetail.getTotalPrice());
            if (com.alibaba.dubbo.common.utils.StringUtils.isBlank(orderDetail.getNote())) {
                tableMap.put("订单备注:", "--");
            } else {
                tableMap.put("订单备注:", orderDetail.getNote());
            }
            // 获取表格内容
            List<GiftGoodsPageFTInstance> giftGoodsPageFTInstances = orderDetail.getOrderGoodsDetailList();
            for (int i = 0; i < giftGoodsPageFTInstances.size(); i++) {
                GiftGoodsPageFTInstance ftIns = giftGoodsPageFTInstances.get(i);
                String[] column = new String[nameString.length];
                column[0] = String.valueOf((i + 1));
                column[1] = ftIns.getGoodsName();
                column[2] = ftIns.getBuyNum().toString();
                column[3] = ftIns.getPrice();
                column[4] = ftIns.getTotalPrice();
                info.add(column);
            }

            // 生成Excel表
            ExcelUtil.ExcelExportData setInfo = new ExcelUtil.ExcelExportData();
            setInfo.setTitles(nameString);
            setInfo.setColumns(info);
            setInfo.setTableDetail(tableMap);
            Map<String,Object> xssMap = new HashMap<>();
            xssMap.put("订单详情",setInfo);
            XSSFWorkbook wb = ExcelUtil.exportExcelFile(xssMap);

            //写出文件到服务器
            File f = new File(userdir);
            if (!f.exists()) {
                f.mkdirs();
            } else {
                CommUtil.deleteDirectory(userdir);
                f.mkdirs();
            }
            filePath = userdir + orderNum + ".xlsx";
            FileOutputStream fout = new FileOutputStream(filePath);
            wb.write(fout);
            fout.close();
            if (!StringUtils.isBlank(filePath)) {
                response.setContentType("application/ms-excel");
                response.setHeader("Content-Disposition", "attachment;Filename=" + orderNum + ".xlsx");
                OutputStream out;
                try {
                    out = response.getOutputStream();
                    FileInputStream fis = new FileInputStream(filePath);
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
                    LOGGER.error("exeUpload", e);
                }
            }
        } catch (Exception e) {
            br.setErrorMessage("999", "下载失败");
            LOGGER.error("exeUpload", e);
        }
    }
}
