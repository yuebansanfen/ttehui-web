package com.mocentre.tehui.common.filter;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.core.utils.response.BaseResult;

public class SessionFilter implements Filter {

    private static String path;
    private String        ignoredUrl;

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String conPath = req.getContextPath();
        String reqUrl = req.getRequestURI();
        Object user = req.getSession().getAttribute(SessionKeyConstant.USER);
        if (user == null && !pass(reqUrl)) {
            if ("".equals(conPath) || null == conPath) {
                conPath = "/";
            }
            boolean isAjax = CommUtil.isAjax(request);
            if (isAjax) {
                BaseResult br = new BaseResult();
                br.setErrorMessage("400", "登入过期，请重新登入");
                res.setCharacterEncoding("UTF-8");
                res.getWriter().write(br.toJsonString());
            } else {
                res.getWriter().write(
                        "<script type='text/javascript'>window.location.href='" + conPath + "login.html';</script>");
            }
        } else {
            chain.doFilter(request, response);
            return;
        }
    }

    private Boolean pass(String url) {
        String[] igUrl = ignoredUrl.split(",");
        for (int i = 0; i < igUrl.length; i++) {
            if (url.contains(igUrl[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ignoredUrl = filterConfig.getInitParameter("ignored");
        setPath(filterConfig.getServletContext().getRealPath(File.separator));
    }

    public static void setPath(String path) {

        SessionFilter.path = path;
    }

    public static String getPath() {

        return path;
    }

}
