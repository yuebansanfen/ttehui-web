package com.mocentre.tehui.core.utils;

import com.mocentre.tehui.core.utils.response.BaseResult;

/**
 * 类CodeBuildHelper.java的实现描述：错误代码格式化工具类
 * 
 * @author Arvin 2015年4月26日 上午1:36:22
 */
public class CodeBuildHelper {

    public static void build(String code, String msg, BaseResult result) {
        result.setErrorMessage(code, msg);
    }

    public static void build(String code, String msg, BaseResult result, Object... args) {
        result.setSuccess(false);
        result.setErrorMessage(code, msg);
        try {
            result.setMessage(String.format(result.getMessage(), args));
        } catch (Exception e) {
        }
    }

}
