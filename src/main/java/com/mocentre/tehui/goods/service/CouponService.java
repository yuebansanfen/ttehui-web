/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.goods.service;

import com.mocentre.common.*;
import com.mocentre.tehui.CategoryManageService;
import com.mocentre.tehui.CouponManageService;
import com.mocentre.tehui.backend.model.CategoryInstance;
import com.mocentre.tehui.backend.model.CouponDetailInstance;
import com.mocentre.tehui.backend.model.CouponInstance;
import com.mocentre.tehui.backend.model.GoodsInstance;
import com.mocentre.tehui.backend.param.CouponDetailQueryParam;
import com.mocentre.tehui.backend.param.CouponParam;
import com.mocentre.tehui.backend.param.CouponQueryParam;
import com.mocentre.tehui.common.service.BaseBackendService;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.common.util.ExcelUtil;
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
 * 类CouponService.java的实现描述：发现页service
 *
 * @author yukaiji 2016年11月14日
 */
@Component
public class CouponService extends BaseBackendService {

    @Autowired
    private CouponManageService couponManageService;
    @Autowired
    private CategoryManageService categoryManageService;

    public List<CategoryInstance> queryCategory() {
        ListResult<CategoryInstance> listResult = categoryManageService.getAllCategoryList(generateRequestId());
        return listResult.getData();
    }

    /**
     * 查询所有的优惠券
     *
     * @return
     */
    public ListJsonResult<CouponInstance> queryCouponPage(CouponQueryParam couponParam, Object shopId) {
        couponParam.setShopId(Long.valueOf(shopId.toString()));
        ListJsonResult<CouponInstance> result = couponManageService.queryCouponPage(couponParam);
        return result;
    }

    /**
     * 查询所有的优惠券详情
     *
     * @return
     */
    public ListJsonResult<CouponDetailInstance> queryCouponDetailPage(CouponDetailQueryParam couponParam, Long id) {
        couponParam.setRequestId(generateRequestId());
        ListJsonResult<CouponDetailInstance> result = couponManageService.queryCouponDetailPage(couponParam);
        return result;
    }

    /**
     * 添加
     *
     * @param couponParam
     * @return
     */
    public BaseResult addCoupon(CouponParam couponParam) {
        BaseResult br = new BaseResult();
        BaseResult result = couponManageService.addCoupon(couponParam);
        if (!result.isSuccess()) {
            br.setErrorMessage(result.getCode(), result.getMessage());
        }
        return br;
    }

    /**
     * 修改
     *
     * @param couponParam
     * @return
     */
    public BaseResult editCoupon(CouponParam couponParam) {
        BaseResult br = couponManageService.updateCoupon(couponParam);
        if (!br.isSuccess()) {
            br.setErrorMessage("100", "修改失败");
        }
        return br;
    }

    public BaseResult delete(String idList) {
        BaseResult br = new BaseResult();
        String[] sIdList = idList.split(",");
        List<Long> lIdList = new ArrayList<Long>();
        for (String id : sIdList) {
            lIdList.add(Long.valueOf(id));
        }
        br = couponManageService.deleteCoupon(lIdList, generateRequestId());
        if (!br.isSuccess()) {
            br.setErrorMessage("100", "修改失败");
        }
        return br;
    }

    public PlainResult<CouponInstance> queryCouponById(Long id, Long shopId) {
        return couponManageService.getCoupon(id, shopId, generateRequestId());
    }

    public PlainResult<Map<String, Object>> prviewEdit(Long id, Long shopId) {
        PlainResult<Map<String, Object>> pr = new PlainResult<Map<String, Object>>();
        Map<String, Object> prMap = new HashMap<String, Object>();
        CouponInstance coupon = null;
        List<CategoryInstance> categoryList = new ArrayList<CategoryInstance>();
        List<GoodsInstance> goodsList = new ArrayList<GoodsInstance>();
        MapResult result = couponManageService.preEdit(id, shopId, generateRequestId());
        if (result.isSuccess()) {
            coupon = (CouponInstance) result.getData().get("coupon");
            categoryList = (List<CategoryInstance>) result.getData().get("categroyList");
            Object goodsInsList = result.getData().get("goodsInsList");
            if (goodsInsList != null) {
                goodsList = (List<GoodsInstance>) goodsInsList;
            }
        } else {
            pr.setErrorMessage(result.getCode(), result.getMessage());
            return pr;
        }

        prMap.put("coupon", coupon);
        prMap.put("categoryList", categoryList);
        prMap.put("goodsList", goodsList);
        pr.setData(prMap);
        return pr;
    }

    public String uploadCoupon(long couponId, String userdir) {
        String filePath = null;
        // 标题
        String[] nameString = {"优惠券名称", "优惠券金额", "优惠券码", "优惠券密码"};
        // 每个sheet的固定长度
        Integer length = 10000;
        // 请求参数
        CouponDetailQueryParam param = new CouponDetailQueryParam();
        param.setId(couponId);
        param.setStart(0);
        param.setLength(length);
        ListJsonResult<CouponDetailInstance> result = null;
        Map<String, Object> xssMap = new HashMap<>();
        // sheet下标
        int sheetIndex  = 1;
        try{
            while (true) {
                result = couponManageService.queryCouponDetailPage(param);
                param.setStart(param.getStart()+length);
                List<CouponDetailInstance> detailList = result.getData();
                if (detailList != null && detailList.size() != 0) {
                    List<String[]> info = new ArrayList<>();
                    for (CouponDetailInstance instance : detailList) {
                        String[] column = new String[nameString.length];
                        column[0] = instance.getCouponDes();
                        column[1] = instance.getCouponMoney().toString();
                        column[2] = instance.getCouponSn();
                        column[3] = "";
                        info.add(column);
                    }
                    ExcelUtil.ExcelExportData setInfo = new ExcelUtil.ExcelExportData();
                    setInfo.setTitles(nameString);
                    setInfo.setColumns(info);
                    xssMap.put("优惠券详情"+sheetIndex, setInfo);
                }
                sheetIndex++;
                if (result.getData().size() < length) {
                    break;
                }
            }

            // 生成Excel表
            XSSFWorkbook wb = ExcelUtil.exportExcelFile(xssMap);
            //写出文件到服务器
            File f = new File(userdir);
            if (!f.exists()) {
                f.mkdirs();
            } else {
                CommUtil.deleteDirectory(userdir);
                f.mkdirs();
            }
            filePath = userdir + "优惠券" + ".xlsx";
            FileOutputStream fout = new FileOutputStream(filePath);
            wb.write(fout);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
            result.setErrorMessage("999", "系统异常");
        }
        return filePath;
    }
}
