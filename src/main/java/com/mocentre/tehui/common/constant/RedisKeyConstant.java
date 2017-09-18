/*
 * Copyright 2016 gaoshou360.com All right reserved. This software is the
 * confidential and proprietary information of gaoshou360.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with gaoshou360.com .
 */
package com.mocentre.tehui.common.constant;

/**
 * 类RedisKeyConstant.java的实现描述：缓存key值前缀
 * 
 * @author sz.gong 2016年3月22日 下午4:16:21
 */
public class RedisKeyConstant {

    /** 用户信息key前缀 **/
    public static String USER_UUID               = "uuid";

    public static String SEED                    = "seed";

    /** 短信验证码key前缀 **/
    public static String VERIFICATION_CODE       = "vcode";
    /** 短信验证码发送次数前缀 **/
    public static String VERIFICATION_CODE_COUNT = "vcode_count";
    /** 微信jsTicket前缀 **/
    public static String WX_JSAPI_TICKET         = "string_moshop_jsapi_ticket";

    /** 活动接入账户 **/
    public static String ACCOUNT_MEB             = "accountmeb";

    /** 登入次数key前缀 **/
    public static String LOGIN_COUNT             = "integer_login_count";

}
