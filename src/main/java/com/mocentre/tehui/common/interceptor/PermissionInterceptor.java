/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.interceptor;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.mocentre.tehui.backend.model.RuleInstance;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.core.utils.response.BaseResult;

/**
 * 类PermissionInterceptor.java的实现描述：权限拦截器
 * 
 * @author sz.gong 2017年6月28日 下午3:08:33
 */
public class PermissionInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURL().toString();
        List<RuleInstance> ruleList = (List<RuleInstance>) request.getSession().getAttribute(SessionKeyConstant.MENU);
        if (ruleList != null && !ruleList.isEmpty()) {
            for (RuleInstance rule : ruleList) {
                String rUrl = rule.getUrl();
                if (StringUtils.isBlank(rUrl) || !url.contains(rUrl)) {
                    continue;
                }
                return true;
            }
        }
        boolean isAjax = CommUtil.isAjax(request);
        if (isAjax) {
            BaseResult br = new BaseResult();
            br.setErrorMessage("300", "暂无权限");
            writeMessageUtf8(response, br);
        } else {
            response.sendRedirect("/sys/login/noPermission");
        }
        return false;
    }

    private void writeMessageUtf8(HttpServletResponse response, BaseResult br) {
        PrintWriter out = null;
        try {
            response.setContentType("application/json;charset=UTF-8");
            out = response.getWriter();
            out.write(br.toJsonString());
        } catch (Exception e) {

        } finally {
            if (out != null) {
                out.print("");
                out.close();
            }
        }
    }
}
