/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.constant;

/**
 * 类SystemConstant.java的实现描述：配置文件常量
 * 
 * @author sz.gong 2016年5月18日 上午10:50:35
 */
public class ConfigConstant {

    /** 商铺logo存放目录 **/
    public static String SHOP_ROOT                   = "shop_root";

    /** 发现页图片存放目录 **/
    public static String DISC_ROOT                   = "disc_root";

    /** 专题图片存放目录 **/
    public static String SUB_ROOT                    = "sub_root";

    /** nginx访问路径 **/
    public static String N_STATIC                    = "nginx_static";

    /** 静态资源url **/
    public static String STATIC_URL                  = "static_url";

    /** cookie一级域名 **/
    public static String TL_COOKIE_DOMAIN            = "tl_cookie_domain";

    /** vue环境 **/
    public static String VUE_SHOP_BASE_URL           = "vue_shop_base_url";
    public static String VUE_SHOP_STATIC_URL         = "vue_shop_static_url";

    public static String VUE_GIFT_BASE_URL           = "vue_gift_base_url";
    public static String VUE_GIFT_STATIC_URL         = "vue_gift_static_url";

    public static String VUE_ABCSHOP_BASE_URL        = "vue_abcshop_base_url";
    public static String VUE_ABCSHOP_STATIC_URL      = "vue_abcshop_static_url";

    /***
     * 又拍云上传参数
     */
    public static String UPYUN_BUCKET                = "upyun_bucket";
    public static String UPYUN_API_KEY               = "upyun_api_key";
    public static String UPYUN_SHOWIMG_DOMAIN        = "upyun_showImg_domain";
    public static String UPYUN_USERNAME              = "upyun_username";
    public static String UPYUN_PASSWORD              = "upyun_password";

    /***
     * 微信小程序
     */
    public static String APPID                       = "appid";
    public static String SECRET                      = "secret";

    /**
     * 农行支付结果页面URL地址(天天特惠商城)
     */
    public static String ABC_RESULT_SUCCESS_URL      = "abc_result_success_url";
    public static String ABC_RESULT_FAIL_URL         = "abc_result_fail_url";
    public static String ABC_ORDER_URL               = "abc_order_url";

    /**
     * 农行支付结果页面URL地址(农行商城)
     */
    public static String ABC_MALL_RESULT_SUCCESS_URL = "abc_mall_result_success_url";
    public static String ABC_MALL_RESULT_FAIL_URL    = "abc_mall_result_fail_url";
    public static String ABC_MALL_ORDER_URL          = "abc_mall_order_url";

    /**
     * 短信配置
     */
    public static String SMS_JSON_DATA               = "sms_json_data";

    /**
     * 农行活动大转盘配置
     */
    public static String LOTTERY_JSON_DATA           = "lottery_json_code";
    public static String LOTTERY_URL                 = "lottery_url";
    public static String LOTTERY_CRYPT_KEY           = "lottery_crypt_key";

    /**
     * 商城活动大转盘配置
     */
    public static String LOTTERY_SHOP_JSON_DATA      = "lottery_shop_json_data";
    public static String LOTTERY_SHOP_CRYPT_KEY      = "lottery_shop_crypt_key";

    /**
     * 内部签名
     */
    public static String INNER_SIGN_APPID            = "inner_sign_appid";
    public static String INNER_SIGN_APPSECRET        = "inner_sign_appsecret";

    /**
     * 微信公众号
     */
    public static String WX_APPID                    = "wx_appid";
    public static String WX_APPSECRET                = "wx_appsecret";

    /**
     * 微信授权
     */
    public static String WX_OAUTH2_CODE_URL          = "wx_oauth2_code_url";
    public static String WX_OAUTH2_TOKEN_URL         = "wx_oauth2_token_url";
    public static String WX_CALLBACK_URL             = "wx_callback_url";
    public static String WX_CASH_URL                 = "wx_cash_url";
    public static String WX_TD_CALLBACK_URL          = "wx_td_callback_url";

    /**
     * 每日优鲜跳转url
     */
    public static String MISSFRESH_ORDER_URL         = "missfresh_order_url";
    public static String MISSFRESH_CART_URL          = "missfresh_cart_url";

    /**
     * 第三方农行客户端支付结果页
     */
    public static String ABC_TD_RESULT_URL           = "abc_td_result_url";

    /**
     * 第三方农行网页支付结果页
     */
    public static String ABC_TD_RESULT_SUCCESS_URL   = "abc_td_result_success_url";
    public static String ABC_TD_RESULT_FAIL_URL      = "abc_td_result_fail_url";

}
