/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.buy.service;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.OrderManageService;
import com.mocentre.tehui.backend.model.OrderDetailInstance;
import com.mocentre.tehui.backend.model.OrderInstance;
import com.mocentre.tehui.backend.param.LogisticsParam;
import com.mocentre.tehui.backend.param.OrderQueryParam;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.core.utils.DateUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类OrderService.java的实现描述：订单service
 *
 * @author sz.gong 2016年11月11日 下午2:36:54
 */
@Component
public class OrderService {

    @Autowired
    private OrderManageService orderMagService;

    public ListJsonResult<OrderInstance> queryOrderPage(OrderQueryParam orderQueryParam, Long shopId) {
        orderQueryParam.setShopId(shopId);
        ListJsonResult<OrderInstance> result = orderMagService.queryPage(orderQueryParam);
        return result;
    }

    public PlainResult<Map<String, Object>> queryOrderDetail(Long id, Long shopId) {
        PlainResult<Map<String, Object>> br = new PlainResult<Map<String, Object>>();
        Map<String, Object> brMap = new HashMap<String, Object>();
        PlainResult<OrderInstance> result = orderMagService.queryDetail(id, shopId);
        if (result == null) {
            br.setErrorMessage("101", "接口异常");
            return br;
        }
        if (!result.isSuccess()) {
            br.setErrorMessage("102", "接口调用失败");
            return br;
        }
        OrderInstance orderIns = result.getData();
        brMap.put("order", orderIns);
        brMap.put("detail", orderIns.getDetailInstanceList());
        brMap.put("logistics", orderIns.getLogisticsInstancesList());
        br.setData(brMap);
        return br;
    }

    /**
     * 填写物流发货
     *
     * @param lgParam
     * @return
     */
    public PlainResult<String> sendOrder(LogisticsParam lgParam) {
        PlainResult<String> pr = new PlainResult<String>();
        PlainResult<String> result = orderMagService.deliver(lgParam);
        if (result == null) {
            pr.setErrorMessage("101", "提交失败");
        } else {
            pr = result;
        }
        return pr;
    }

    /**
     * 编辑物流发货
     *
     * @param lgParam
     * @return
     */
    public BaseResult editSendOrder(LogisticsParam lgParam) {
        BaseResult br = new BaseResult();
        BaseResult result = orderMagService.editDeliver(lgParam);
        if (result == null) {
            br.setErrorMessage("101", "提交失败");
            return br;
        }
        if (!result.isSuccess()) {
            br.setErrorMessage("102", "提交失败");
            return br;
        }
        return br;
    }

    public BaseResult orderRefund(String orderNum, String orderDetailNum, String requestId) {
        BaseResult br = orderMagService.orderRefund(orderNum, orderDetailNum, requestId);
        return br;
    }

//    public String uploadOrder(String beginTime, String endTime, String payType, Long shopId, String userdir) {
//        String filePath = null;
//        // 标题
//        String[] nameString = { "订单编号", "订单时间", "订单金额", "订单备注", "收件人", "联系电话", "收货地址", "配送费", "商品名称", "购买数量", "商品单价",
//                "商品总价" };
//        // 内容
//
//        ListResult<OrderInstance> result = orderMagService.queryOrderList(beginTime, endTime, payType, shopId);
//        List<OrderInstance> orderDetail = result.getData();
//        try {
//            Map<String, Object> xssMap = new HashMap<String, Object>();
//            if (orderDetail.size() <= 0) {
//                ExcelUtil.ExcelExportData setInfo = new ExcelUtil.ExcelExportData();
//                Map<String, Object> tableMap = new HashMap<String, Object>();
//                tableMap.put("订单信息:", "暂无相关订单");
//                setInfo.setTableDetail(tableMap);
//                xssMap.put("导出订单", setInfo);
//            }
//            List<String[]> info = new ArrayList<>();
//            for (OrderInstance orderInstance : orderDetail) {
//                // 获取表格内容
//                List<OrderDetailInstance> orderDetailInstances = orderInstance.getDetailInstanceList();
//                for (int i = 0; i < orderDetailInstances.size(); i++) {
//                    boolean notOneRow = i > 0 ;
//                    OrderDetailInstance ins = orderDetailInstances.get(i);
//                    String[] column = new String[nameString.length];
//                    column[0] = notOneRow ? "" : orderInstance.getOrderNum();
//                    column[1] = notOneRow ? "" : DateUtils.formatTime(orderInstance.getOrderTime());
//                    column[2] = notOneRow ? "" : orderInstance.getTotalPrice().toString();
//                    column[3] = notOneRow ? "" : (orderInstance.getRemark() == null ? "" : orderInstance.getRemark());
//                    column[4] = notOneRow ? "" : orderInstance.getRecipient();
//                    column[5] = notOneRow ? "" : orderInstance.getTelephone();
//                    column[6] = notOneRow ? "" : orderInstance.getAddress();
//                    column[7] = orderInstance.getTransFee().toString();
//                    column[8] = ins.getGoodsName();
//                    column[9] = ins.getGoodsAmount().toString();
//                    column[10] = ins.getGoodsRealPrice().toString();
//                    column[11] = String.valueOf(Float.valueOf(ins.getGoodsRealPrice().toString())
//                            * ins.getGoodsAmount());
//                    info.add(column);
//                }
//            }
//            ExcelUtil.ExcelExportData setInfo = new ExcelUtil.ExcelExportData();
//            setInfo.setTitles(nameString);
//            setInfo.setColumns(info);
//            xssMap.put("订单详情", setInfo);
//            // 生成Excel表
//            XSSFWorkbook wb = ExcelUtil.exportExcelFile(xssMap);
//            //写出文件到服务器
//            File f = new File(userdir);
//            if (!f.exists()) {
//                f.mkdirs();
//            } else {
//                CommUtil.deleteDirectory(userdir);
//                f.mkdirs();
//            }
//            filePath = userdir + "订单" + ".xlsx";
//            FileOutputStream fout = new FileOutputStream(filePath);
//            wb.write(fout);
//            fout.close();
//        } catch (Exception e) {
//            result.setErrorMessage("999", "系统异常");
//        }
//        return filePath;
//    }

    public String uploadOrder(String beginTime, String endTime, String payType, Long shopId, String userdir) {
        String filePath = null;
        // 标题
        String[] nameString = {"序号", "订单编号", "订单时间", "订单金额", "订单备注", "收件人", "联系电话", "收货地址", "配送费", "商品名称", "购买数量", "商品单价",
                "商品总价" };
        int columNum = 0;
        int SerialNum = 1;
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("订单详情");
        XSSFRow row = sheet.createRow(columNum++);
        for (int i = 0; i < nameString.length; i++) {
            XSSFCell titleCell = row.createCell(i);
            titleCell.setCellValue(nameString[i]);
        }
        ListResult<OrderInstance> result = orderMagService.queryOrderList(beginTime, endTime, payType, shopId);
        List<OrderInstance> orderDetail = result.getData(); //所有的订单
        try {
            for (OrderInstance orderInstance : orderDetail) {
                List<OrderDetailInstance> orderDetailInstanceList = orderInstance.getDetailInstanceList();
                int odSize = orderDetailInstanceList.size();
                if (odSize > 1 ){
                    sheet.addMergedRegion(new CellRangeAddress(columNum,columNum + odSize - 1,0,0));
                    sheet.addMergedRegion(new CellRangeAddress(columNum,columNum + odSize - 1,1,1));
                    sheet.addMergedRegion(new CellRangeAddress(columNum,columNum + odSize - 1,2,2));
                    sheet.addMergedRegion(new CellRangeAddress(columNum,columNum + odSize - 1,3,3));
                    sheet.addMergedRegion(new CellRangeAddress(columNum,columNum + odSize - 1,4,4));
                    sheet.addMergedRegion(new CellRangeAddress(columNum,columNum + odSize - 1,5,5));
                    sheet.addMergedRegion(new CellRangeAddress(columNum,columNum + odSize - 1,6,6));
                    sheet.addMergedRegion(new CellRangeAddress(columNum,columNum + odSize - 1,7,7));
                    sheet.addMergedRegion(new CellRangeAddress(columNum,columNum + odSize - 1,8,8));
                    for (int i = 0; i < odSize; i++) {
                        XSSFRow columRow = sheet.createRow(columNum++);
                        OrderDetailInstance ins = orderDetailInstanceList.get(i);
                        if (i == 0){
                            columRow.createCell(0).setCellValue(SerialNum++);
                            columRow.createCell(1).setCellValue(orderInstance.getOrderNum());
                            columRow.createCell(2).setCellValue(DateUtils.formatTime(orderInstance.getOrderTime()));
                            columRow.createCell(3).setCellValue(orderInstance.getTotalPrice().toString());
                            columRow.createCell(4).setCellValue(orderInstance.getRemark() == null ? "" : orderInstance.getRemark());
                            columRow.createCell(5).setCellValue(orderInstance.getRecipient());
                            columRow.createCell(6).setCellValue(orderInstance.getTelephone());
                            columRow.createCell(7).setCellValue(orderInstance.getAddress());
                            columRow.createCell(8).setCellValue(orderInstance.getTransFee().toString());
                        }
                        columRow.createCell(9).setCellValue(ins.getGoodsName());
                        columRow.createCell(10).setCellValue(ins.getGoodsAmount().toString());
                        columRow.createCell(11).setCellValue(ins.getGoodsRealPrice().toString());
                        columRow.createCell(12).setCellValue(String.valueOf(Float.valueOf(ins.getGoodsRealPrice().toString())
                                * ins.getGoodsAmount()));
                    }
                }else {
                    XSSFRow columRow = sheet.createRow(columNum++);
                    OrderDetailInstance ins = orderDetailInstanceList.get(0);
                    columRow.createCell(0).setCellValue(SerialNum++);
                    columRow.createCell(1).setCellValue(orderInstance.getOrderNum());
                    columRow.createCell(2).setCellValue(DateUtils.formatTime(orderInstance.getOrderTime()));
                    columRow.createCell(3).setCellValue(orderInstance.getTotalPrice().toString());
                    columRow.createCell(4).setCellValue(orderInstance.getRemark() == null ? "" : orderInstance.getRemark());
                    columRow.createCell(5).setCellValue(orderInstance.getRecipient());
                    columRow.createCell(6).setCellValue(orderInstance.getTelephone());
                    columRow.createCell(7).setCellValue(orderInstance.getAddress());
                    columRow.createCell(8).setCellValue(orderInstance.getTransFee().toString());
                    columRow.createCell(9).setCellValue(ins.getGoodsName());
                    columRow.createCell(10).setCellValue(ins.getGoodsAmount().toString());
                    columRow.createCell(11).setCellValue(ins.getGoodsRealPrice().toString());
                    columRow.createCell(12).setCellValue(String.valueOf(Float.valueOf(ins.getGoodsRealPrice().toString())
                            * ins.getGoodsAmount()));
                }
            }

            //写出文件到服务器
            File f = new File(userdir);
            if (!f.exists()) {
                f.mkdirs();
            } else {
                CommUtil.deleteDirectory(userdir);
                f.mkdirs();
            }
            filePath = userdir + "订单" + ".xlsx";
            FileOutputStream fout = new FileOutputStream(filePath);
            wb.write(fout);
            fout.close();
        } catch (Exception e) {
            result.setErrorMessage("999", "系统异常");
        }
        return filePath;
    }
}
