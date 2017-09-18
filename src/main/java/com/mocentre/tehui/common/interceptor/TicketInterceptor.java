package com.mocentre.tehui.common.interceptor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.mocentre.tehui.common.constant.Constants;
import com.mocentre.tehui.common.util.ThreadTokenHolder;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.utils.response.BaseResult;

/**
 * 类TicketInterceptor.java的实现描述：拦截指定的请求
 * 
 * @author sz.gong 2017年3月2日 下午2:00:28
 */
public class TicketInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisCache redisCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //String allowDomain = SystemConfig.INSTANCE.getValue(ConfigConstant.ALLOW_DOMAIN);
        String ticket = getTicketFromCookies(request);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
        if (StringUtils.isEmpty(ticket)) {
            return handleInvalidTicket(request, response);
        }
        if (!redisCache.checkLoginInfo(ticket)) {
            return handleExpireTicket(request, response);
        }
        ThreadTokenHolder.setToken(ticket);// 保存当前ticket，用于Controller层获取登录用户信息
        return super.preHandle(request, response, handler);
    }

    /**
     * 从cookies中获取ticket值
     * 
     * @param request
     * @return
     */
    private String getTicketFromCookies(HttpServletRequest request) {
        String ticket = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if (Constants.COOKIE_TICKET.equals(cookie.getName())) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
        //        Cookie cookie = WebUtils.getCookie(request, Constants.TICKET);
        //        System.out.println("cookie is null:" + cookie);
        //        if (cookie != null) {
        //            if (Constants.TICKET.equals(cookie.getName())) {
        //                ticket = cookie.getValue();
        //            }
        //        }
        return ticket;
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
