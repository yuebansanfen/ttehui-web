/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.front.service;

import org.springframework.stereotype.Component;

/**
 * 类ToolService.java的实现描述：工具service
 * 
 * @author sz.gong 2016年12月22日 下午2:46:40
 */
@Component
public class ToolService {

    /**
     * 农行K码支付自动提交表单
     * 
     * @param signature
     * @param trustPayUrl
     * @param errorPage
     * @return
     */
    public String abcFormSubmit(String signature, String trustPayUrl, String errorPage) {
        StringBuffer buf = new StringBuffer();
        buf.append("<form id=\"abc_form\" method=\"post\" action=\"" + trustPayUrl + "\">");
        buf.append("<input type=\"hidden\" name=\"MSG\" value=\"" + signature + "\">");
        buf.append("<input type=\"hidden\" name=\"errorPage\" value=\"" + errorPage + "\">");
        buf.append("</form>");
        buf.append("<script>");
        buf.append("document.getElementById('abc_form').submit()");
        buf.append("</script>");
        return buf.toString();
    }
}
