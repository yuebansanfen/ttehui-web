/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.interceptor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.util.concurrent.RateLimiter;
import com.mocentre.tehui.common.util.LoggerUtil;
import com.mocentre.tehui.core.utils.response.BaseResult;

/**
 * 类SmsRateLimitInterceptor.java的实现描述：简易发送短信限流拦截器
 * 
 * @author sz.gong 2017年8月9日 上午10:53:15
 */
public class SmsRateLimitInterceptor implements HandlerInterceptor {

    private RateLimiter rateLimiter;

    public SmsRateLimitInterceptor(int rate) {
        if (rate > 0) {
            rateLimiter = RateLimiter.create(rate);
        } else {
            throw new RuntimeException("rate must greater than zero");
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!rateLimiter.tryAcquire()) {
            LoggerUtil.tehuiwebLog.info(request.getRequestURI() + "请求超过限流器速率");
            BaseResult br = new BaseResult();
            br.setErrorMessage("400", "当前访问人数太多，请稍后再试...");
            writeMessageUtf8(response, br.toJsonString());
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }

    public void setRate(int rate) {
        rateLimiter.setRate(rate);
    }

    private void writeMessageUtf8(HttpServletResponse response, String resultStr) {
        PrintWriter out = null;
        try {
            //response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            out = response.getWriter();
            out.write(resultStr);
        } catch (Exception e) {

        } finally {
            if (out != null) {
                out.print("");
                out.close();
            }
        }
    }

}
