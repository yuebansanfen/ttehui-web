package com.mocentre.tehui.wechat.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.mocentre.common.BaseResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.common.constant.Constants;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.common.util.SMSUtil;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.CustomerInfoFTInstance;
import com.mocentre.tehui.frontend.service.LoginManageService;
import com.mocentre.tehui.wechat.service.ToolUtil;

@Controller
@RequestMapping("/wxa/login")
public class LoginWXController extends BaseController {

    @Autowired
    private LoginManageService loginMagService;
    @Autowired
    private RedisCache         redisCache;

    /**
     * 微信登入
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "loginByWeixin.htm", method = RequestMethod.POST)
    public void loginByWeixin(HttpServletRequest request, HttpServletResponse response) {
        String jsCode = request.getParameter("jsCode");
        PlainResult<String> result = new PlainResult<String>();
        try {
            ToolUtil tool = new ToolUtil();
            String openid = tool.getOpenid(jsCode);
            if (StringUtils.isNotBlank(openid)) {
                PlainResult<CustomerInfoFTInstance> resInfo = loginMagService.customerLoginByWeixin(openid,
                        generageRequestId());
                if (resInfo.isSuccess()) {
                    CustomerInfoFTInstance userInfo = resInfo.getData();
                    String uuidKey = redisCache.saveLoginUser(userInfo);
                    result.setData(uuidKey);
                } else {
                    result.setErrorMessage(resInfo.getCode(), resInfo.getMessage());
                }
            } else {
                result.setErrorMessage("1000", "微信账号未绑定");
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 用户通过账户密码登陆
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "loginByPassword.htm", method = RequestMethod.POST)
    public void loginByPassword(HttpServletRequest request, HttpServletResponse response) {
        String telephone = request.getParameter("telephone");
        String password = request.getParameter("password");
        PlainResult<String> result = new PlainResult<String>();
        try {
            if (StringUtils.isBlank(telephone)) {
                result.setErrorMessage("1000", "手机号不能为空");
            }
            if (StringUtils.isBlank(password)) {
                result.setErrorMessage("1000", "密码不能为空");
            }
            if (result.isSuccess()) {
                PlainResult<CustomerInfoFTInstance> resInfo = loginMagService.customerLoginByPassword(telephone,
                        password, generageRequestId());
                result.setRequestId(resInfo.getRequestId());
                if (resInfo.isSuccess()) {
                    CustomerInfoFTInstance userInfo = resInfo.getData();
                    String uuidKey = redisCache.saveLoginUser(userInfo);
                    result.setData(uuidKey);
                } else {
                    result.setErrorMessage(resInfo.getCode(), resInfo.getMessage());
                }
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
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
        result.setRequestId(generageRequestId());
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
                String res = SMSUtil.sendVerificationCode(telephone, message, true, requestId);
                BaseResult br = JSON.parseObject(res, BaseResult.class);
                br.setRequestId(requestId);
                if (br.isSuccess()) {
                    redisCache.saveVerificationCode(telephone, String.valueOf(verCode));
                } else {
                    redisCache.reduVerificationCodeCount(telephone);
                    result.setErrorMessage("1000", "验证码发送失败");
                }
                //              HttpProtocolHandler handler = HttpProtocolHandler.getInstance();
                //                    HttpRequest req = new HttpRequest(HttpResultType.STRING);
                //                    req.setUrl(config.getValue(ConfigConstant.SMS_HTTPURL));
                //                    req.setMethod(HttpRequest.METHOD_POST);
                //                    req.setCharset("utf-8");
                //                    NameValuePair name = new NameValuePair("name", config.getValue(ConfigConstant.SMS_NAME));
                //                    NameValuePair pass = new NameValuePair("pass", config.getValue(ConfigConstant.SMS_PASSWORD));
                //                    NameValuePair mobiles = new NameValuePair("mobiles", telephone);
                //                    NameValuePair content = new NameValuePair("content", message);
                //                    NameValuePair[] params = new NameValuePair[] { name, pass, mobiles, content };
                //                    req.setParameters(params);
                //                    HttpResponse res = handler.execute(req);
                //                    String strResult = res.getStringResult();
                //                    if (StringUtils.isNotBlank(strResult)) {
                //                        String[] datas = strResult.split(",");
                //                        if ("00".equals(datas[1].replace("\n", ""))) {
                //                            redisCache.saveVerificationCode(telephone, String.valueOf(verCode));
                //                            redisCache.saveVerificationCodeCount(telephone);
                //                        } else {
                //                            result.setErrorMessage("1000", "验证码发送失败");
                //                        }
                //                    }
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统异常");
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
        String telephone = request.getParameter("telephone");
        String vcode = request.getParameter("vcode");
        PlainResult<String> result = new PlainResult<String>();
        try {
            if (StringUtils.isBlank(vcode)) {
                result.setErrorMessage("1000", "验证码不能为空");
            }
            if (StringUtils.isBlank(telephone)) {
                result.setErrorMessage("1000", "电话号码不能为空");
            }
            if (result.isSuccess()) {
                String ovcode = redisCache.getVerificationCode(telephone);
                if (ovcode == null || !StringUtils.equals(ovcode, vcode)) {
                    result.setErrorMessage("1001", "验证码已过期");
                    super.printJson(response, result.toJsonString());
                    return;
                }
                PlainResult<CustomerInfoFTInstance> resInfo = loginMagService.customerLoginByVerificationCode(
                        telephone, vcode, generageRequestId());
                if (result.isSuccess()) {
                    CustomerInfoFTInstance userInfo = resInfo.getData();
                    String uuidKey = redisCache.saveLoginUser(userInfo);
                    result.setData(uuidKey);
                } else {
                    result.setErrorMessage(resInfo.getCode(), resInfo.getMessage());
                }
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

}
