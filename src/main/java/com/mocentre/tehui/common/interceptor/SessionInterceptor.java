package com.mocentre.tehui.common.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.utils.response.BaseResult;

/**
 * 类SessionInterceptor.java的实现描述：电子礼品session过滤
 * 
 * @author sz.gong 2017年4月26日 上午11:28:57
 */
public class SessionInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //String allowDomain = SystemConfig.INSTANCE.getValue(ConfigConstant.ALLOW_DOMAIN);
        Object customerId = request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
        if (StringUtils.isEmpty(customerId)) {
            return handleInvalidTicket(request, response);
        }
        return super.preHandle(request, response, handler);
    }

    private boolean handleInvalidTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BaseResult br = new BaseResult();
        br.setErrorMessage("600", "未登录，请先登录");
        writeMessageUtf8(response, br);
        return false;
    }

    private boolean handleExpireTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BaseResult br = new BaseResult();
        br.setErrorMessage("601", "登录已过期，请重新登录");
        writeMessageUtf8(response, br);
        return false;
    }

    private void writeMessageUtf8(HttpServletResponse response, BaseResult br) throws IOException {
        try {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(br.toJsonString());
        } catch (Exception e) {

        } finally {
            response.getWriter().close();
        }
    }

}
