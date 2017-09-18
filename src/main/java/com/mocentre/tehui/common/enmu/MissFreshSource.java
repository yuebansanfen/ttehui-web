/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.enmu;

/**
 * 类MissFreshSource.java的实现描述：每日优鲜访问来源
 * 
 * @author yukaiji 2017年8月24日 下午14:33:45
 */
public enum MissFreshSource {

    ABCA("abca", "农行掌银客户端"),
    ABCWX("abcwx", "农行微信公众号");

    private String code;
    private String name;

    private MissFreshSource(String code, String name) {
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
        for (MissFreshSource status : MissFreshSource.values()) {
            if (status.code.equals(code)) {
                return status.name;
            }
        }
        return null;
    }

}
