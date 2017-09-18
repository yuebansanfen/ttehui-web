package com.mocentre.tehui.front.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mocentre.common.BaseResult;
import com.mocentre.common.MapResult;
import com.mocentre.common.PlainResult;
import com.mocentre.msite.service.WeChatHttpService;
import com.mocentre.msite.service.impl.WeChatHttpServiceImpl;
import com.mocentre.msite.vo.TicketVo;
import com.mocentre.msite.vo.WeChatConfigVo;
import com.mocentre.tehui.ShareConfigManageService;
import com.mocentre.tehui.backend.model.ShareConfigInstance;
import com.mocentre.tehui.common.SystemConfig;
import com.mocentre.tehui.common.constant.ConfigConstant;
import com.mocentre.tehui.common.constant.Constants;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.common.util.SMSUtil;
import com.mocentre.tehui.common.util.ThreeDES;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.CustomerInfoFTInstance;
import com.mocentre.tehui.frontend.model.ShopCartDetailFTInstance;
import com.mocentre.tehui.frontend.param.ShopCartDetailParam;
import com.mocentre.tehui.frontend.service.LoginManageService;
import com.mocentre.tehui.frontend.service.ShopCartManageService;

/**
 * 登陆 Created by 王雪莹 on 2016/12/24.
 */
@Controller
@RequestMapping("/front/login")
public class LoginFTController extends BaseController {

    @Autowired
    private LoginManageService       loginManageService;
    @Autowired
    private ShareConfigManageService shareConfigManageService;
    @Autowired
    private RedisCache               redisCache;
    @Autowired
    private ShopCartManageService    shopCartManageService;

    /**
     * 用户通过账户密码登陆
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "loginByPassword.htm", method = RequestMethod.POST)
    public void loginByPassword(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        String telephone = request.getParameter("telephone");
        String password = request.getParameter("password");
        try {
            if (StringUtils.isBlank(telephone)) {
                result.setErrorMessage("1000", "电话号码不能为空");
            }
            if (StringUtils.isBlank(password)) {
                result.setErrorMessage("1000", "密码不能为空");
            }
            if (result.isSuccess()) {
                Long count = redisCache.saveLoginCount(telephone);
                if (count > Constants.LOGIN_FAIL_COUNT) {
                    result.setErrorMessage("1002", "登入过于频繁，请稍后再试");
                    super.printJson(response, result.toJsonString());
                    return;
                }
                PlainResult<CustomerInfoFTInstance> pr = loginManageService.customerLoginByPassword(telephone,
                        password, generageRequestId());
                if (pr.isSuccess()) {
                    CustomerInfoFTInstance cumInfo = pr.getData();
                    List<Cookie> cookieList = this.getSeedCookie(cumInfo);
                    if (cookieList != null) {
                        for (Cookie cookie : cookieList) {
                            response.addCookie(cookie);
                        }
                    }
                    redisCache.reduLoginCount(telephone);
                } else {
                    result.setErrorMessage("1001", pr.getMessage());
                }
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 用户通过验证码登陆和注册
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "loginByVerificationCode.htm", method = RequestMethod.POST)
    public void loginByVerificationCode(HttpServletRequest request, HttpServletResponse response) {
        //PlainResult<CustomerInfoFTInstance> result = new PlainResult<CustomerInfoFTInstance>();
        BaseResult result = new BaseResult();
        String telephone = request.getParameter("telephone");
        String verificationCode = request.getParameter("verificationCode");
        try {
            if (StringUtils.isBlank(telephone)) {
                result.setErrorMessage("1000", "电话号码不能为空");
            }
            if (StringUtils.isBlank(verificationCode)) {
                result.setErrorMessage("1000", "验证码不能为空");
            }
            if (result.isSuccess()) {
                String ovcode = redisCache.getVerificationCode(telephone);
                if (ovcode == null || !StringUtils.equals(ovcode, verificationCode)) {
                    result.setErrorMessage("1001", "验证码已过期");
                } else {
                    PlainResult<CustomerInfoFTInstance> pr = loginManageService.customerLoginByVerificationCode(
                            telephone, verificationCode, generageRequestId());
                    if (pr.isSuccess()) {
                        CustomerInfoFTInstance cumInfo = pr.getData();
                        List<Cookie> cookieList = this.getSeedCookie(cumInfo);
                        if (cookieList != null) {
                            for (Cookie cookie : cookieList) {
                                response.addCookie(cookie);
                            }
                        }
                    } else {
                        result.setErrorMessage("1002", pr.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    private List<Cookie> getSeedCookie(CustomerInfoFTInstance cumInfo) throws Exception {
        List<Cookie> cookieList = new ArrayList<Cookie>();
        SystemConfig config = SystemConfig.INSTANCE;
        String cookieDomain = config.getValue(ConfigConstant.TL_COOKIE_DOMAIN);
        String uuidKey = redisCache.saveLoginUser(cumInfo);
        Cookie cookie = new Cookie(Constants.COOKIE_TICKET, uuidKey);
        cookie.setDomain(cookieDomain);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookieList.add(cookie);
        String lotteryData = config.getValue(ConfigConstant.LOTTERY_SHOP_JSON_DATA);
        String lotteryCrypt = config.getValue(ConfigConstant.LOTTERY_SHOP_CRYPT_KEY);
        JSONObject jobj = JSON.parseObject(lotteryData);
        String lottery = jobj.getString("lotteryKey");
        String token = jobj.getString("token");
        String userId = cumInfo.getTelephone();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("token", token);
        params.put("lotteryKey", lottery);
        params.put("userId", userId);
        String cookieData = ThreeDES.encode(JSON.toJSONString(params), lotteryCrypt);
        Cookie cookieLottery = new Cookie(Constants.COOKIE_LOTTERY_ACCESS, cookieData);
        cookieLottery.setDomain(cookieDomain);
        cookieLottery.setPath("/");
        cookieLottery.setMaxAge(7 * 24 * 60 * 60);
        cookieList.add(cookieLottery);
        //        CookieGenerator cookie = new CookieGenerator();
        //        cookie.setCookieDomain(Constants.COOKIE_DOMAIN);
        //        cookie.setCookieMaxAge(7 * 24 * 60 * 60);
        //        cookie.setCookieName(Constants.TICKET);
        //        cookie.addCookie(response, URLEncoder.encode(uuidKey, "UTF-8"));
        return cookieList;
    }

    /**
     * 为该手机号发送验证码
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "sendVerificationCode.htm", method = RequestMethod.POST)
    public void sendVerificationCode(HttpServletRequest request, HttpServletResponse response) {
        String telephone = request.getParameter("telephone");
        BaseResult result = new BaseResult();
        try {
            if (StringUtils.isBlank(telephone)) {
                result.setErrorMessage("1000", "电话号码不能为空");
            }
            if (result.isSuccess()) {
                Long sendCount = redisCache.saveVerificationCodeCount(telephone);
                if (sendCount > Constants.SMS_COUNT) {
                    result.setErrorMessage("1001", "验证码发送次数过于频繁，请稍后再试！");
                    super.printJson(response, result.toJsonString());
                    return;
                }
                int verCode = CommUtil.randomSixInt();
                String message = "【天天特惠】尊敬的用户，您好，您的验证码为" + verCode + "，验证码有效时间为5分钟，请及时输入！";
                String requestId = generageRequestId();
                String res = SMSUtil.sendVerificationCode(telephone, message, true, generageRequestId());
                BaseResult br = JSON.parseObject(res, BaseResult.class);
                br.setRequestId(requestId);
                if (br.isSuccess()) {
                    redisCache.saveVerificationCode(telephone, String.valueOf(verCode));
                } else {
                    redisCache.reduVerificationCodeCount(telephone);
                    result.setErrorMessage("1002", "验证码发送失败");
                }
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统异常");
            e.printStackTrace();
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 分享
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "share.htm")
    public void share(HttpServletRequest request, HttpServletResponse response) {
        MapResult mr = new MapResult();
        Map<String, Object> resMap = new HashMap<String, Object>();
        mr.setRequestId(generageRequestId());
        String fromUrl = request.getParameter("fromurl");
        try {
            if (StringUtils.isBlank(fromUrl)) {
                mr.setErrorMessage("1000", "参数错误");
            }
            if (mr.isSuccess()) {
                int count = fromUrl.indexOf("#");
                if (count > 0) {
                    fromUrl = fromUrl.substring(0, count);
                }
                WeChatConfigVo chatConfig = null;
                WeChatHttpService chatService = new WeChatHttpServiceImpl(fromUrl);
                String ticket = redisCache.getWxJSticket();
                if (StringUtils.isBlank(ticket)) {
                    TicketVo ticketVo = chatService.getTicketVo();
                    if (ticketVo != null) {
                        ticket = ticketVo.getTicket();
                        redisCache.saveJSticket(ticket);
                    }
                }
                if (StringUtils.isNotBlank(ticket)) {
                    ShareConfigInstance shareConfig = null;
                    chatConfig = chatService.getWeChatConfig(ticket);
                    PlainResult<ShareConfigInstance> pr = shareConfigManageService
                            .getDefaultShareConfig(generageRequestId());
                    if (pr.isSuccess()) {
                        shareConfig = pr.getData();
                    }
                    resMap.put("defConfig", shareConfig);
                    resMap.put("chatConfig", chatConfig);
                    mr.setData(resMap);
                } else {
                    mr.setErrorMessage("1001", "分享失败");
                }
            }
        } catch (Exception e) {
            mr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, mr.toJsonString());
    }

    @RequestMapping(value = "fastLogin.htm", method = RequestMethod.POST)
    public void fastLogin(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        String telephone = request.getParameter("telephone");
        String verificationCode = request.getParameter("verificationCode");
        List<ShopCartDetailFTInstance> scdFTInsList = null;
        Object shopCartListObj = request.getSession().getAttribute(SessionKeyConstant.SHOPPING_CART_LIST);
        if (shopCartListObj != null) {
            scdFTInsList = (List<ShopCartDetailFTInstance>) shopCartListObj;
        }
        try {
            if (StringUtils.isBlank(telephone)) {
                result.setErrorMessage("1000", "电话号码不能为空");
            }
            if (StringUtils.isBlank(verificationCode)) {
                result.setErrorMessage("1000", "验证码不能为空");
            }
            if (result.isSuccess()) {
                String ovcode = redisCache.getVerificationCode(telephone);
                if (ovcode == null || !StringUtils.equals(ovcode, verificationCode)) {
                    result.setErrorMessage("1001", "验证码已过期");
                } else {
                    List<ShopCartDetailParam> scdParamList = new ArrayList<ShopCartDetailParam>();
                    if (scdFTInsList != null) {
                        for (ShopCartDetailFTInstance scdIns : scdFTInsList) {
                            ShopCartDetailParam scdp = new ShopCartDetailParam();
                            scdp.setActGoodsId(scdIns.getActGoodsId());
                            scdp.setBuyNum(scdIns.getBuyNum());
                            scdp.setGoodsId(scdIns.getGoodsId());
                            scdp.setGoodsName(scdIns.getGoodsName());
                            scdp.setGoodsSku(scdIns.getGoodsSku());
                            scdp.setGoodsSkuDes(scdIns.getGoodsSkuDes());
                            scdp.setShopId(scdIns.getShopId());
                            scdp.setShopName(scdIns.getShopName());
                            scdp.setShowLogo(scdIns.getShowLogo());
                            scdp.setOldPrice(scdIns.getOldPrice());
                            scdp.setSellPrice(scdIns.getSellPrice());
                            scdParamList.add(scdp);
                        }
                    }
                    PlainResult<CustomerInfoFTInstance> pr = loginManageService.loginByCodeAndSyncCart(telephone,
                            verificationCode, scdParamList, generageRequestId());
                    if (pr.isSuccess()) {
                        CustomerInfoFTInstance cumInfo = pr.getData();
                        List<Cookie> cookieList = this.getSeedCookie(cumInfo);
                        if (cookieList != null) {
                            for (Cookie cookie : cookieList) {
                                response.addCookie(cookie);
                            }
                        }
                    } else {
                        result.setErrorMessage("1002", pr.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }
}
