/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.enmu;

/**
 * MissFreshRedirect.java的实现描述：每日优鲜跳转类型
 * 
 * @author yukaiji 2017年8月24日 下午14:33:45
 */
public enum MissFreshRedirect {

    ORDER("order", "每日优鲜订单页面"),
    CART("cart", "每日优鲜购物车页面");

    private String code;
    private String name;

    private MissFreshRedirect(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCodeValue() {
        return this.code;
    }

    public String getNameValue() {
        return this.name;
    }

    public static String getName(String code) {
        for (MissFreshRedirect status : MissFreshRedirect.values()) {
            if (status.code.equals(code)) {
                return status.name;
            }
        }
        return null;
    }

}
