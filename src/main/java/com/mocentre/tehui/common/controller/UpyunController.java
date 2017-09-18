/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.controller;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.common.SystemConfig;
import com.mocentre.tehui.common.constant.ConfigConstant;
import com.mocentre.tehui.common.upyun.YunFormUploader;
import com.mocentre.tehui.common.upyun.YunParams;
import com.mocentre.tehui.core.controller.BaseController;

/**
 * 类UpyunAction.java的实现描述：上传文件到又拍云
 * 
 * @author sz.gong 2017年1月9日 上午11:33:23
 */
@Controller
@RequestMapping("/common/upyun")
public class UpyunController extends BaseController {

    /**
     * 上传图片
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "uploadImg.htm", method = RequestMethod.GET)
    public String uploadImg(HttpServletRequest request, HttpServletResponse response, Model model) {
        String cxtPath = request.getContextPath();
        String path = "https://" + request.getServerName() + cxtPath;
        String nodeId = request.getParameter("nodeId");
        Map<String, Object> paramsMap = new LinkedHashMap<String, Object>();
        Long time = new Date().getTime();
        paramsMap.put(YunParams.SAVE_KEY, "/uploads/{year}/{mon}/{day}/" + time + "_{random}{.suffix}");
        paramsMap.put(YunParams.ALLOW_FILE_TYPE, "gif,jpeg,jpg,png");
        paramsMap.put(YunParams.RETURN_URL, path + "/common/upyun/uploadImgReturn?nodeId=" + nodeId);
        paramsMap.put(YunParams.CONTENT_LENGTH_RANGE, "0,1024000");
        String bucketName = SystemConfig.INSTANCE.getValue(ConfigConstant.UPYUN_BUCKET);
        String apiKey = SystemConfig.INSTANCE.getValue(ConfigConstant.UPYUN_API_KEY);
        YunFormUploader upYun = new YunFormUploader(bucketName, apiKey);
        String policy = upYun.getPolicy(paramsMap);
        String signature = upYun.getSignature(policy);
        String action = upYun.getAction();
        model.addAttribute("signature", signature);
        model.addAttribute("policy", policy);
        model.addAttribute("action", action);
        return "/commons/upload/upyun";
    }

    /**
     * 上传图片回调
     * 
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "uploadImgReturn", method = RequestMethod.GET)
    public String uploadImgReturn(HttpServletRequest request, HttpServletResponse response, Model model) {
        String apiKey = SystemConfig.INSTANCE.getValue(ConfigConstant.UPYUN_API_KEY);
        String showDomain = SystemConfig.INSTANCE.getValue(ConfigConstant.UPYUN_SHOWIMG_DOMAIN);
        Boolean success = true;
        String errorMsg = null;
        String nodeId = request.getParameter("nodeId");
        String code = request.getParameter("code");
        String url = request.getParameter("url");
        String message = request.getParameter("message");
        String time = request.getParameter("time");
        String sign = request.getParameter("sign");
        String noSign = request.getParameter("non-sign");
        if (StringUtils.isBlank(code) || StringUtils.isBlank(url) || StringUtils.isBlank(message)
                || StringUtils.isBlank(time)) {
            success = false;
        } else {
            if (StringUtils.isNotEmpty(sign)) {
                String signMd5 = DigestUtils.md5Hex(code + "&" + message + "&" + url + "&" + time + "&" + apiKey);
                if (signMd5.equals(sign)) {
                    if (code.equals("200")) {
                        success = true;
                    } else {
                        success = false;
                        errorMsg = message;
                    }
                } else {
                    success = false;
                    errorMsg = "签名错误，上传失败";
                }
            } else if (StringUtils.isNotEmpty(noSign)) {
                String noSignMd5 = DigestUtils.md5Hex(code + "&" + message + "&" + url + "&" + time);
                if (noSignMd5.equals(noSign)) {
                    success = true;
                } else {
                    success = false;
                    errorMsg = "签名错误，上传失败";
                }
            } else {
                success = false;
                errorMsg = message;
            }
        }
        model.addAttribute("isHandled", 1);
        model.addAttribute("success", success);
        model.addAttribute("errorMsg", errorMsg);
        model.addAttribute("url", showDomain + url);
        model.addAttribute("nodeId", nodeId);
        return "/commons/upload/upyun";
    }

    /**
     * 上传图片回调
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "frontUploadImgReturn", method = RequestMethod.GET)
    public void frontUploadImgReturn(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<String> br = new PlainResult<String>();
        br.setRequestId(generageRequestId());
        try {
            String apiKey = SystemConfig.INSTANCE.getValue(ConfigConstant.UPYUN_API_KEY);
            String showDomain = SystemConfig.INSTANCE.getValue(ConfigConstant.UPYUN_SHOWIMG_DOMAIN);
            Boolean success = true;
            String errorMsg = null;
            String code = request.getParameter("code");
            String url = request.getParameter("url");
            String message = request.getParameter("message");
            String time = request.getParameter("time");
            String sign = request.getParameter("sign");
            String noSign = request.getParameter("non-sign");
            if (StringUtils.isBlank(code) || StringUtils.isBlank(url) || StringUtils.isBlank(message)
                    || StringUtils.isBlank(time)) {
                success = false;
            } else {
                if (StringUtils.isNotEmpty(sign)) {
                    String signMd5 = DigestUtils.md5Hex(code + "&" + message + "&" + url + "&" + time + "&" + apiKey);
                    if (signMd5.equals(sign)) {
                        if (code.equals("200")) {
                            success = true;
                        } else {
                            success = false;
                            errorMsg = message;
                        }
                    } else {
                        success = false;
                        errorMsg = "签名错误，上传失败";
                    }
                } else if (StringUtils.isNotEmpty(noSign)) {
                    String noSignMd5 = DigestUtils.md5Hex(code + "&" + message + "&" + url + "&" + time);
                    if (noSignMd5.equals(noSign)) {
                        success = true;
                    } else {
                        success = false;
                        errorMsg = "签名错误，上传失败";
                    }
                } else {
                    success = false;
                    errorMsg = message;
                }
            }
            if (!success) {
                br.setErrorMessage("1001", errorMsg);
            } else {
                br.setData(showDomain + url);
            }
        } catch (Exception e) {
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

}
