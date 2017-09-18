package com.mocentre.gift.buy.controller.service;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.mocentre.common.PlainResult;
import com.mocentre.gift.backend.model.GiftOrderGoodsDetailInstance;
import com.mocentre.gift.backend.model.GiftOrderInfoInstance;
import com.mocentre.gift.backend.service.GiftOrderMangeService;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.common.util.ExcelUtil;
import com.mocentre.tehui.core.utils.DateUtils;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单service
 *
 * Created by yukaiji on 2017/5/9.
 */

@Component
public class GiftOrderService {

    @Autowired
    private GiftOrderMangeService orderMangeService;

    public String uploadOrder(Long id, String userdir, String orderNum, String requestId) {
        String filePath = null;
        // 标题
        String[] nameString = { "序号", "商品名称", "购买数量", "商品单价", "商品总价" };
        // 内容
        List<String[]> info = new ArrayList<>();
        PlainResult<GiftOrderInfoInstance> result = orderMangeService.queryOrderDetail(id, requestId);
        GiftOrderInfoInstance orderDetail = result.getData();
        try {
            // 订单基本信息
            Map<String, Object> tableMap = new HashMap<String, Object>();
            tableMap.put("订单编号:", orderDetail.getOrderNum());
            tableMap.put("订单时间:", DateUtils.formatTime(orderDetail.getOrderTime()));
            tableMap.put("订单金额:", orderDetail.getTotalPrice());
            if (StringUtils.isBlank(orderDetail.getNote())) {
                tableMap.put("订单备注:", "--");
            } else {
                tableMap.put("订单备注:", orderDetail.getNote());
            }
            // 获取表格内容
            List<GiftOrderGoodsDetailInstance> giftGoodsInstances = orderDetail.getOrderGoodsDetailList();
            for (int i = 0; i < giftGoodsInstances.size(); i++) {
                GiftOrderGoodsDetailInstance ins = giftGoodsInstances.get(i);
                String[] column = new String[nameString.length];
                column[0] = String.valueOf((i + 1));
                column[1] = ins.getGoodsName();
                column[2] = ins.getGoodsAmount().toString();
                column[3] = ins.getGoodsPrice();
                column[4] = String.valueOf(Float.valueOf(ins.getGoodsPrice()) * ins.getGoodsAmount());
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
        } catch (Exception e) {
            result.setErrorMessage("999", "系统异常");
        }
        return filePath;
    }
}
