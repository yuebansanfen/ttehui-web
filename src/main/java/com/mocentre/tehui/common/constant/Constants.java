/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.constant;

/**
 * 类Constants.java的实现描述：常量
 * 
 * @author sz.gong 2017年2月14日 上午9:40:56
 */
public class Constants {

    public static String  SESSIONID             = "sessionid";

    public static String  COOKIE_TICKET         = "TICKET";

    /**
     * Cookie一级域名
     */
    public static String  COOKIE_DOMAIN         = ".mocentre.cn";

    /**
     * 单位时间内短信发送最多允许次数
     */
    public static Integer SMS_COUNT             = 3;

    /**
     * 大转盘抽奖Cookie
     */
    public static String  COOKIE_LOTTERY_ACCESS = "LOTTERY_ACCESS";

    /**
     * 微信用户openid缓存Cookie
     */
    public static String  COOKIE_USER_OPENID    = "USER_OPENID";

    /**
     * 单位时间内登入失败最多允许次数
     */
    public static Integer LOGIN_FAIL_COUNT      = 5;

}
