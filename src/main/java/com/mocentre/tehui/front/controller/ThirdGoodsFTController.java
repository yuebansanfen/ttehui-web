/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.front.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mocentre.common.PlainResult;
import com.mocentre.commons.SignUtil;
import com.mocentre.tehui.backend.model.MemberAccountKeyInstance;
import com.mocentre.tehui.common.SystemConfig;
import com.mocentre.tehui.common.constant.ConfigConstant;
import com.mocentre.tehui.common.enmu.MissFreshRedirect;
import com.mocentre.tehui.common.enmu.MissFreshSource;
import com.mocentre.tehui.common.util.LoggerUtil;
import com.mocentre.tehui.common.util.MapConvert;
import com.mocentre.tehui.common.util.ThreeDES;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.CustomerInfoFTInstance;
import com.mocentre.tehui.frontend.model.TdActAndSpecialGoodsFTInstance;
import com.mocentre.tehui.frontend.model.TdGoodsFTInstance;
import com.mocentre.tehui.frontend.model.ThirdGoodsFTInstance;
import com.mocentre.tehui.frontend.service.ThirdGoodsManageService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * ThirdGoodsFTController.java的实现描述：第三方商品
 *
 * @author sz.gong 2017年6月20日 下午1:56:03
 */
@Controller
@RequestMapping("/front/thirdGoods")
public class ThirdGoodsFTController extends BaseController {

    @Autowired
    private ThirdGoodsManageService thirdGoodsManageService;
    @Autowired
    private RedisCache              redisCache;

    /**
     * 投放城市商品查询
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "getGoodsListByCityCode.htm", method = RequestMethod.POST)
    public void orderQuery(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<TdActAndSpecialGoodsFTInstance> pr = new PlainResult<>();
        try {
            String keyMark = request.getParameter("keymark");
            String cityCode = request.getParameter("bdCityCode");
            String longitude = request.getParameter("longitude");
            String latitude = request.getParameter("latitude");

            // 用户信息及参数验证
            CustomerInfoFTInstance customerInfo = redisCache.currentLoginUser();
            if (customerInfo == null) {
                pr.setErrorMessage("1001", "用户登入过期");
                super.printJson(response, pr.toJsonString());
                return;
            }
            String userId = customerInfo.getAbcaid();
            boolean paramValid = StringUtils.isBlank(cityCode) || StringUtils.isBlank(userId)
                    || StringUtils.isBlank(longitude) || StringUtils.isBlank(latitude)
                    || StringUtils.isBlank(keyMark);
            if (paramValid) {
                pr.setErrorMessage("1000", "参数错误");
            }

            // 获取接口请求来源
            String userAgent = request.getHeader("user-agent").toLowerCase();
            String origin = MissFreshSource.ABCA.getCodeValue();
            if (userAgent.indexOf("micromessenger") > 0) {
                origin = MissFreshSource.ABCWX.getCodeValue();
                userId = DigestUtils.md5Hex(userId);
            }

            if (pr.isSuccess()) {
                PlainResult<TdGoodsFTInstance> result = thirdGoodsManageService.getThirdGoodsByBDCityCode(cityCode,
                        generageRequestId());
                if (!result.isSuccess()) {
                    pr.setErrorMessage("1002", result.getMessage());
                }

                // 农行来源地理位置转换
                String provinceCode = "";
                if (MissFreshSource.ABCA.getCodeValue().equals(origin)) {
                    cityCode = MapConvert.getTxCitycode(cityCode);
                    provinceCode = MapConvert.getTxProvincecode(cityCode);
                    Map<String, Double> map = MapConvert.BDTXLatLngConvert(Double.parseDouble(latitude),
                            Double.parseDouble(longitude));
                    longitude = map.get("lng").toString();
                    latitude = map.get("lat").toString();
                }

                TdGoodsFTInstance tdGoods = result.getData();
                if (tdGoods != null) {
                    List<ThirdGoodsFTInstance> actGoodsList = tdGoods.getActGoodsList();
                    List<ThirdGoodsFTInstance> specialGoodsList = tdGoods.getSpecialGoodsList();
                    MemberAccountKeyInstance mebAut = redisCache.getMebAccount(keyMark);
                    String appKey = mebAut.getAppKey();
                    String appSerect = mebAut.getAppSecret();
                    HashMap<String, String> paramMap = new HashMap<>();

                    // 签名用到的固定参数
                    paramMap.put("longitude", longitude);
                    paramMap.put("latitude", latitude);
                    paramMap.put("cityCode", cityCode);
                    paramMap.put("provinceCode", provinceCode);
                    paramMap.put("appKey", appKey);
                    paramMap.put("origin", origin);
                    paramMap.put("id", userId);

                    thirdGoodsSignUrl(actGoodsList, appSerect, paramMap);
                    thirdGoodsSignUrl(specialGoodsList, appSerect, paramMap);
                    TdActAndSpecialGoodsFTInstance tdGoodsFTIns = new TdActAndSpecialGoodsFTInstance();
                    tdGoodsFTIns.setActGoodsList(actGoodsList);
                    tdGoodsFTIns.setSpecialGoodsList(specialGoodsList);
                    pr.setData(tdGoodsFTIns);
                }
            }
        } catch (Exception e) {
            pr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 访问商品跳转地址签名
     */
    private void thirdGoodsSignUrl(List<ThirdGoodsFTInstance> thirdGoodsInsList, String appSerect,
                                   HashMap<String, String> paramMap) {
        for (ThirdGoodsFTInstance tdGoods : thirdGoodsInsList) {
            StringBuffer bufUrl = new StringBuffer();
            String linkUrl = tdGoods.getLinkUrl();
            int index = linkUrl.lastIndexOf("?");
            if (index > 0) {
                String param = linkUrl.substring(index + 1, linkUrl.length());
                String[] paramArray = param.split("&");
                for (int i = 0; i < paramArray.length; i++) {
                    String[] paramsKV = paramArray[i].split("=");
                    if (paramsKV.length == 2) {
                        paramMap.put(paramsKV[0].trim(), paramsKV[1].trim());
                    }
                }
            }
            bufUrl.append(index > 0 ? linkUrl.substring(0, index) : linkUrl);
            String sign = SignUtil.buildMysign(appSerect, paramMap);
            bufUrl.append("?");
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                bufUrl.append(entry.getKey()).append("=").append(entry.getValue());
                bufUrl.append("&");
            }
            bufUrl.append("sign=").append(sign);
            tdGoods.setLinkUrl(bufUrl.toString());
        }
    }

    @RequestMapping(value = "redirectOrderOrCart.htm")
    public String redirectOrderOrShopCart(HttpServletRequest request, Model model) {
        StringBuffer redirectUrl = new StringBuffer();
        try {
            String keyMark = request.getParameter("keymark");
            String cityCode = request.getParameter("bdCityCode");
            String longitude = request.getParameter("longitude");
            String latitude = request.getParameter("latitude");
            String destination = request.getParameter("destination");

            CustomerInfoFTInstance customerInfo = redisCache.currentLoginUser();
            if (customerInfo == null) {
                model.addAttribute("errorMsg", "用户不存在");
                return getErrorIndex();
            }
            String userId = customerInfo.getAbcaid();
            String userAgent = request.getHeader("user-agent").toLowerCase();
            String origin = MissFreshSource.ABCA.getCodeValue();
            if (userAgent.indexOf("micromessenger") > 0) {
                origin = MissFreshSource.ABCWX.getCodeValue();
                userId = DigestUtils.md5Hex(userId);
            }

            boolean paramValid = StringUtils.isBlank(cityCode) || StringUtils.isBlank(userId)
                    || StringUtils.isBlank(longitude) || StringUtils.isBlank(latitude)
                    || StringUtils.isBlank(keyMark) || StringUtils.isBlank(destination);
            if (paramValid) {
                model.addAttribute("errorMsg", "参数错误");
                return getErrorIndex();
            }
            if (MissFreshRedirect.ORDER.getCodeValue().equals(destination)) {
                redirectUrl.append(SystemConfig.INSTANCE.getValue(ConfigConstant.MISSFRESH_ORDER_URL));
            }
            if (MissFreshRedirect.CART.getCodeValue().equals(destination)) {
                redirectUrl.append(SystemConfig.INSTANCE.getValue(ConfigConstant.MISSFRESH_CART_URL));
            }

            // 农行来源地理位置转换
            String provinceCode = "";
            if (MissFreshSource.ABCA.getCodeValue().equals(origin)) {
                cityCode = MapConvert.getTxCitycode(cityCode);
                provinceCode = MapConvert.getTxProvincecode(cityCode);
                Map<String, Double> map = MapConvert.BDTXLatLngConvert(Double.parseDouble(latitude),
                        Double.parseDouble(longitude));
                longitude = map.get("lng").toString();
                latitude = map.get("lat").toString();
            }

            MemberAccountKeyInstance mebAut = redisCache.getMebAccount(keyMark);
            if (mebAut == null) {
                model.addAttribute("errorMsg", "第三方账户不存在");
                return getErrorIndex();
            }
            String appKey = mebAut.getAppKey();
            String appSerect = mebAut.getAppSecret();

            HashMap<String, String> paramMap = new HashMap<>();

            // 签名用到的固定参数
            paramMap.put("longitude", longitude);
            paramMap.put("latitude", latitude);
            paramMap.put("cityCode", cityCode);
            paramMap.put("provinceCode", provinceCode);
            paramMap.put("appKey", appKey);
            paramMap.put("origin", origin);
            paramMap.put("id", userId);

            String sign = SignUtil.buildMysign(appSerect, paramMap);
            redirectUrl.append("?");
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                redirectUrl.append(entry.getKey()).append("=").append(entry.getValue());
                redirectUrl.append("&");
            }
            redirectUrl.append("sign=").append(sign);
        } catch (Exception e) {
            model.addAttribute("errorMsg", "系统错误");
            return getErrorIndex();
        }
        return "redirect:" + redirectUrl;
    }


    /**
     * 大转盘URL跳转
     *
     * @param request
     */
    @RequestMapping(value = "redirectLottery.htm", method = RequestMethod.GET)
    public String lottery(HttpServletRequest request, HttpServletResponse response) {
        String sendUrl = "";
        try {
            CustomerInfoFTInstance customerInfo = redisCache.currentLoginUser();
            SystemConfig config = SystemConfig.INSTANCE;
            sendUrl = config.getValue(ConfigConstant.LOTTERY_URL);
            String sendJsonData = config.getValue(ConfigConstant.LOTTERY_JSON_DATA);
            String cryptKey = config.getValue(ConfigConstant.LOTTERY_CRYPT_KEY);
            JSONObject obj = JSON.parseObject(sendJsonData);
            String lotteryKey = obj.getString("lotteryKey");
            String token = obj.getString("token");
            String fromType = obj.getString("fromType");
            String userType = obj.getString("userType");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 1);
            Date date = calendar.getTime();
            String time = String.valueOf(date.getTime());
            time = time.substring(0, 10);
            String[] keys = {"ADS", "VKE", "REO", "CFE", "ZFE", "BLE", "VQA", "WQF", "DVZ", "FEQ"};
            time = time + keys[Integer.valueOf(time.substring(time.length() - 1, time.length()))];
            Map<String, Object> sendParam = new HashMap<String, Object>();
            sendParam.put("userId", customerInfo.getAbcaid());
            sendParam.put("phone", "");
            sendParam.put("fromType", fromType);
            sendParam.put("userType", userType);
            sendParam.put("lotteryKey", lotteryKey);
            sendParam.put("token", token);
            sendParam.put("time", time);
            String sendStr = ThreeDES.encode(JSON.toJSONString(sendParam), cryptKey);
            LoggerUtil.tehuiwebLog.info("跳转" + DateUtil.formatDate(new Date()) + " " + sendStr);
            sendStr = sendStr.replace(" ", "").replace("-", "+");
            sendUrl = sendUrl + "/" + sendStr + "/version/2";
        } catch (Exception e) {
            LoggerUtil.tehuiwebLog.error("redirectLottery", e);
        }
        return "redirect:" + sendUrl;
    }
}
