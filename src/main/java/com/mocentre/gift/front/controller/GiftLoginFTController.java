package com.mocentre.gift.front.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.mocentre.common.BaseResult;
import com.mocentre.common.PlainResult;
import com.mocentre.gift.frontend.model.GiftCustomerFTInstance;
import com.mocentre.gift.frontend.service.GiftLoginManageService;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.common.util.SMSUtil;
import com.mocentre.tehui.core.controller.BaseController;

/**
 * 前端登录调用接口Controller
 * <p>
 * Created by yukaiji on 2017/04/12.
 */
@Controller
@RequestMapping("/giftFront/login")
public class GiftLoginFTController extends BaseController {

    @Autowired
    private GiftLoginManageService giftLoginManageService;

    /**
     * 用户名密码登录
     */
    @RequestMapping(value = "loginByPassword.htm", method = RequestMethod.POST)
    public void loginByPassword(HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        PlainResult<String> result = new PlainResult<String>();
        try {
            if (StringUtils.isBlank(userName)) {
                result.setErrorMessage("1000", "用户名不能为空");
            }
            if (StringUtils.isBlank(password)) {
                result.setErrorMessage("1000", "密码不能为空");
            }
            if (result.isSuccess()) {
                PlainResult<GiftCustomerFTInstance> pr = giftLoginManageService.customerLoginByPassword(userName,
                        password, generageRequestId());
                result.setRequestId(pr.getRequestId());
                if (pr.isSuccess()) {
                    GiftCustomerFTInstance customerFTIns = pr.getData();
                    if (StringUtils.isBlank(customerFTIns.getTelephone())) {
                        result.setData("isNull");
                    } else {
                        result.setData("unNull");
                    }
                    request.getSession().setAttribute(SessionKeyConstant.GIFTCUSTOMER_ID, customerFTIns.getId());
                } else {
                    result.setErrorMessage("1001", pr.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.error("loginByPassword.htm", e);
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
                int verCode = CommUtil.randomSixInt();
                String message = "【摩森特礼品平台】短信验证码为：" + verCode + "，半小时内有效";
                String requestId = generageRequestId();
                String res = SMSUtil.sendVerificationCode(telephone, message, true, requestId);
                BaseResult br = JSON.parseObject(res, BaseResult.class);
                br.setRequestId(requestId);
                if (br.isSuccess()) {
                    request.getSession().setAttribute(SessionKeyConstant.VERIFYCODE, String.valueOf(verCode));
                } else {
                    result.setErrorMessage("1000", "验证码发送失败");
                }
                System.out.println(request.getSession().getAttribute(SessionKeyConstant.VERIFYCODE));
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 退出登录
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "logout.htm", method = RequestMethod.POST)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        try {
            HttpSession session = request.getSession();
            session.removeAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
            session.invalidate();
        } catch (Exception e) {
            LOGGER.error("logout.htm", e);
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }
}
