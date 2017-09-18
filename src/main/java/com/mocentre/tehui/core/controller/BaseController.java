/*
 *
 *  * Copyright 2016 mocentre.com All right reserved. This software is the
 *  * confidential and proprietary information of mocentre.com ("Confidential
 *  * Information"). You shall not disclose such Confidential Information and shall
 *  * use it only in accordance with the terms of the license agreement you entered
 *  * into with mocentre.com .
 *
 */

package com.mocentre.tehui.core.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mocentre.common.backend.BaseQueryParam;
import com.mocentre.tehui.core.model.BaseEntity;
import com.mocentre.tehui.core.param.BaseParam;
import com.mocentre.tehui.core.utils.BeanUtils;

public abstract class BaseController {

    protected Logger LOGGER = Logger.getLogger(this.getClass());

    protected <T extends BaseEntity> T bindEntity(HttpServletRequest request, Class<T> clazz) {
        T entity = null;
        try {
            entity = clazz.newInstance();
        } catch (InstantiationException e) {
            LOGGER.error("bindEntity", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("bindEntity", e);
        }
        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String propertyName = (String) enumeration.nextElement();
            String propertyValue = request.getParameter(propertyName).trim();
            propertyValue = propertyValue.replace("\'", "\"");
            try {
                BeanUtils.setBeanPropertyByName(entity, propertyName, propertyValue);
            } catch (Exception e) {
                LOGGER.error("bindEntity", e);
            }
        }
        return entity;
    }

    protected <T> T bindClass(HttpServletRequest request, Class<T> clazz) {
        T params = null;
        try {
            params = clazz.newInstance();
        } catch (InstantiationException e) {
            LOGGER.error("bindClass", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("bindClass", e);
        }
        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String propertyName = (String) enumeration.nextElement();
            String propertyValue = request.getParameter(propertyName).trim();
            propertyValue = propertyValue.replace("\'", "\"");
            try {
                BeanUtils.setBeanPropertyByName(params, propertyName, propertyValue);
            } catch (Exception e) {
                LOGGER.error("bindClass", e);
            }
        }
        return params;
    }

    protected <T extends BaseParam> T bindParamClass(HttpServletRequest request, Class<T> clazz) {
        T entity = null;
        try {
            entity = clazz.newInstance();
        } catch (InstantiationException e) {
            LOGGER.error("bindParamClass", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("bindParamClass", e);
        }
        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String propertyName = (String) enumeration.nextElement();
            String propertyValue = request.getParameter(propertyName).trim();
            propertyValue = propertyValue.replace("\'", "\"");
            try {
                BeanUtils.setBeanPropertyByName(entity, propertyName, propertyValue);
            } catch (Exception e) {
                LOGGER.error("bindParamClass", e);
            }
        }
        return entity;
    }

    /**
     * datatables查询参数bind
     */
    protected <T extends BaseQueryParam> T bindDTParamClass(HttpServletRequest request, Class<T> clazz) {
        T entity = null;
        try {
            entity = clazz.newInstance();
        } catch (InstantiationException e) {
            LOGGER.error("bindDTParam", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("bindDTParam", e);
        }
        String indexStr = request.getParameter("order[0][column]");
        Integer index = indexStr == null ? null : Integer.parseInt(indexStr);
        if (index != null) {
            String column = "columns[" + index + "][data]";
            entity.setOrderColumn(request.getParameter(column));
        }
        entity.setOrderBy(request.getParameter("order[0][dir]"));
        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String propertyName = (String) enumeration.nextElement();
            String propertyValue = request.getParameter(propertyName).trim();
            propertyValue = propertyValue.replace("\'", "\"");
            if ("order[0][dir]".equals(propertyName) || "order[0][column]".equals(propertyName)) {
                continue;
            }
            try {
                BeanUtils.setBeanPropertyByName(entity, propertyName, propertyValue);
            } catch (Exception e) {
                LOGGER.error("bindDTParam", e);
            }
        }
        return entity;
    }

    protected Map<String, String> bindMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String name = (String) enumeration.nextElement();
            String value = request.getParameter(name);
            // 如果是排序列，并且列名带点的，则两边加双引号
            if ("sort".equals(name)) {
                if (value.indexOf(".") > -1) {
                    value = "\"" + value + "\"";
                }
            }
            value = value.replace("\'", "\"");
            LOGGER.debug("NAME:" + name + ", VALUE:" + value);
            map.put(name, value);
        }
        return map;
    }

    protected Map<String, Object> bindMapObj(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String name = (String) enumeration.nextElement();
            String value = request.getParameter(name);
            // 如果是排序列，并且列名带点的，则两边加双引号
            if ("sort".equals(name)) {
                if (value.indexOf(".") > -1) {
                    value = "\"" + value + "\"";
                }
            }
            value = value.replace("\'", "\"");
            LOGGER.debug("NAME:" + name + ", VALUE:" + value);
            map.put(name, value);
        }
        return map;
    }

    /**
     * 直接输出纯Json.
     */
    protected void printJson(HttpServletResponse response, String text) {
        doPrint(response, text, "application/json;charset=UTF-8");
    }

    /**
     * 直接输出纯XML.
     */
    protected void printXML(HttpServletResponse response, String text) {
        doPrint(response, text, "text/xml;charset=UTF-8");
    }

    /**
     * 直接输出纯HTML.
     */
    protected void printHtml(HttpServletResponse response, String text) {
        doPrint(response, text, "text/html;charset=UTF-8");
    }

    /**
     * 直接输出纯字符串.
     */
    protected void printText(HttpServletResponse reponse, String text) {
        doPrint(reponse, text, "text/plain;charset=UTF-8");
    }

    /**
     * 直接输出.
     * 
     * @param contentType
     *            内容的类型.html,text,xml的值见后，json为"text/x-json;charset=UTF-8"
     */
    private void doPrint(HttpServletResponse response, String text, String contentType) {

        PrintWriter out = null;
        try {
            LOGGER.debug("输出的字符串: " + text + "");
            response.setContentType(contentType);
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers",
                    "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
            out = response.getWriter();
            out.write(text);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                out.print("");
                out.close();
            }
        }
    }

    /**
     * 直接输出纯Jsonp.
     */
    protected void printJsonp(HttpServletResponse response, HttpServletRequest request, String text) {
        doPrintP(response, request, text, "application/json;charset=UTF-8");
    }

    private void doPrintP(HttpServletResponse response, HttpServletRequest request, String text, String contentType) {

        PrintWriter out = null;
        try {
            LOGGER.debug("输出的字符串: " + text + "");
            String callback = request.getParameter("callback");
            response.setContentType(contentType);
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers",
                    "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
            out = response.getWriter();
            out.write(callback + "(" + text + ")");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                out.print("");
                out.close();
            }
        }

    }

    /**
     * @description：根据传入的字段名称typeStr获取其get方法名称
     * @method：getMethodNameByType
     */
    protected String getMethodNameByType(String typeStr) {
        String firstStr = typeStr.substring(0, 1).toUpperCase();
        String methodNameString = "get" + firstStr + typeStr.substring(1);
        return methodNameString;
    }

    /**
     * @method：strutsMapByObj
     * @param obj Object对象
     * @return Map<String, Object>
     * @description：将javaBean(obj)对象的属性及值组装到Map<String, Object>
     */
    protected Map<String, Object> strutsMapByObj(Object obj) throws NoSuchFieldException {
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            String nameString = field.getName();
            if (field.getType().isAssignableFrom(String.class))
                map.put(nameString, BeanUtils.forceGetProperty(obj, nameString));
            else
                map.put(nameString, BeanUtils.forceGetProperty(obj, String.valueOf(nameString)));
        }
        return map;
    }

    /**
     * 以json格式写入文件
     * 
     * @param filePath 文件全路径
     * @param jsonStr json格式字符串
     */
    protected String writeToJson(String filePath, String jsonStr) {
        String returnFile = null;
        Writer write = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            char[] stack = new char[1024];
            int top = -1;
            StringBuffer sb = new StringBuffer();
            char[] charArray = jsonStr.toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                char c = charArray[i];
                if ('{' == c || '[' == c) {
                    stack[++top] = c;
                    sb.append("\n" + charArray[i] + "\n");
                    for (int j = 0; j <= top; j++) {
                        sb.append("\t");
                    }
                    continue;
                }
                if ((i + 1) <= (charArray.length - 1)) {
                    char d = charArray[i + 1];
                    if ('}' == d || ']' == d) {
                        top--;
                        sb.append(charArray[i] + "\n");
                        for (int j = 0; j <= top; j++) {
                            sb.append("\t");
                        }
                        continue;
                    }
                }
                if (',' == c) {
                    sb.append(charArray[i] + "");
                    for (int j = 0; j <= top; j++) {
                        sb.append("");
                    }
                    continue;
                }
                sb.append(c);
            }
            write = new FileWriter(file);
            write.write(sb.toString());
            returnFile = filePath;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                if (write != null) {
                    write.flush();
                    write.close();
                }
            } catch (IOException e) {
            }
        }
        return returnFile;
    }

    protected String getNameSpace() {
        String ns = null;
        RequestMapping r = getClass().getAnnotation(RequestMapping.class);
        ns = r.value()[0];
        if (!ns.endsWith("/")) {
            ns += "/";
        }
        return ns;
    }

    protected String getErrorIndex() {
        return "/commons/error/index";
    }

    protected String getErrorWap() {
        return "/commons/error/wap";
    }

    protected String generageRequestId() {
        return UUID.randomUUID().toString();
    }
}
