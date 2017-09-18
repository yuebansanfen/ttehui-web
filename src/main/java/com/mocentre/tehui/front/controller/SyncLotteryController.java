/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.front.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mocentre.common.BaseResult;
import com.mocentre.tehui.common.SystemConfig;
import com.mocentre.tehui.common.constant.ConfigConstant;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.param.SyncLotteryParam;
import com.mocentre.tehui.frontend.service.CouponManageService;
import com.mocentre.util.ApiServerUtil;

/**
 * 类SyncCouponController.java的实现描述：同步抽奖
 * 
 * @author sz.gong 2017年7月27日 下午3:51:02
 */
@Controller
@RequestMapping("/front/syncLottery")
public class SyncLotteryController extends BaseController {

    @Autowired
    private CouponManageService couponManagerService;

    @RequestMapping(value = "add.htm", method = RequestMethod.POST)
    public void index(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            Map<String, String> signMap = new HashMap<String, String>();
            Enumeration enumeration = request.getParameterNames();
            while (enumeration.hasMoreElements()) {
                String propertyName = (String) enumeration.nextElement();
                String propertyValue = request.getParameter(propertyName).trim();
                signMap.put(propertyName, propertyValue);
            }
            String itemData = request.getParameter("item");
            JSONObject jobj = JSON.parseObject(itemData);
            String telephone = jobj.getString("telephone");
            String couponSn = jobj.getString("couponSn");
            String type = jobj.getString("type");
            String prizeName = jobj.getString("prizeName");
            String prizeImg = jobj.getString("prizeImg");
            String startTime = jobj.getString("startTime");
            String endTime = jobj.getString("endTime");
            boolean paramValid = StringUtils.isBlank(telephone) || StringUtils.isBlank(type)
                    || StringUtils.isBlank(endTime);
            if (!paramValid) {//实物
                paramValid = "3".equals(type) && StringUtils.isBlank(prizeName);
            }
            if (!paramValid) {//优惠券
                paramValid = "4".equals(type) && StringUtils.isBlank(couponSn);
            }
            if (paramValid) {
                br.setErrorMessage("1000", "参数错误");
            }
            if (br.isSuccess()) {
                SystemConfig config = SystemConfig.INSTANCE;
                String appid = config.getValue(ConfigConstant.INNER_SIGN_APPID);
                String appSecret = config.getValue(ConfigConstant.INNER_SIGN_APPSECRET);
                ApiServerUtil apiUtil = new ApiServerUtil(appid, appSecret);
                com.mocentre.util.BaseResult<String> result = apiUtil.checkSign(signMap);
                if (result != null && result.isSuccess()) {
                    SyncLotteryParam lotteryParam = new SyncLotteryParam();
                    lotteryParam.setTelephone(telephone);
                    lotteryParam.setType(type);
                    lotteryParam.setCouponSn(couponSn);
                    lotteryParam.setPrizeName(prizeName);
                    lotteryParam.setPrizeImg(prizeImg);
                    lotteryParam.setStartTime(startTime);
                    lotteryParam.setEndTime(endTime);
                    br = couponManagerService.addCouponQueue(lotteryParam);
                } else {
                    br.setErrorMessage("1001", "签名失败");
                }
            }
        } catch (Exception e) {
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }
}
