/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.util;

/**
 * 类ThreadTokenHolder.java的实现描述：保存token
 * 
 * @author sz.gong 2017年2月13日 下午2:00:37
 */
public class ThreadTokenHolder {

    /**
     * 保存当前线程中的token
     */
    private static final ThreadLocal<String> THREAD_TOKEN = new ThreadLocal<String>();

    public static String getToken() {
        return THREAD_TOKEN.get();
    }

    public static void setToken(String token) {
        THREAD_TOKEN.set(token);
    }

    public static void clearToken() {
        THREAD_TOKEN.remove();
    }

}
