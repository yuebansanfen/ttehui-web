/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.interceptor.limit;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.mocentre.tehui.common.util.LoggerUtil;
import com.mocentre.tehui.core.utils.response.BaseResult;

/**
 * 类AbstractSpringMonitor.java的实现描述：公有抽象限流类
 * 
 * @author sz.gong 2017年8月17日 上午10:51:35
 */
public abstract class AbstractSpringMonitor implements HandlerInterceptor, MonitorHandler {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean result = before();
        if (!result) {
            LoggerUtil.tehuiwebLog.info(request.getRequestURI() + "请求超过最大限制");
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
        after();
    }

    private void writeMessageUtf8(HttpServletResponse response, String resultStr) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
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
