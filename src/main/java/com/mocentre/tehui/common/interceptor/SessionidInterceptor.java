package com.mocentre.tehui.common.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
 * 类SessionidInterceptor.java的实现描述：拦截指定的请求
 * 
 * @author sz.gong 2016年3月28日 下午3:59:14
 */
public class SessionidInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisCache   redisCache;

    private List<String> allowList; // 放行的URL列表

    //private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String sessionid = getSessionidFromRequest(request);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        if (StringUtils.isEmpty(sessionid)) {
            return handleInvalidSessionid(request, response);
        }
        if (!redisCache.checkLoginInfo(sessionid)) {
            return handleExpireSessionid(request, response);
        }
        ThreadTokenHolder.setToken(sessionid);// 保存当前sessionid，用于Controller层获取登录用户信息
        return super.preHandle(request, response, handler);
    }

    /**
     * 从请求信息中获取sessionid值
     * 
     * @param request
     * @return
     */
    private String getSessionidFromRequest(HttpServletRequest request) {
        // 默认从header里获取sessionid值
        String sessionid = request.getHeader(Constants.SESSIONID);
        if (StringUtils.isEmpty(sessionid)) {
            // 从请求信息中获取sessionid值
            sessionid = request.getParameter(Constants.SESSIONID);
        }
        return sessionid;
    }

    private boolean handleInvalidSessionid(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BaseResult br = new BaseResult();
        br.setErrorMessage("500", "sessionid不能为空");
        writeMessageUtf8(response, br);
        return false;
    }

    private boolean handleExpireSessionid(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BaseResult br = new BaseResult();
        br.setErrorMessage("501", "Session已过期，请重新登录");
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

    public List<String> getAllowList() {
        return allowList;
    }

    public void setAllowList(List<String> allowList) {
        this.allowList = allowList;
    }

}
