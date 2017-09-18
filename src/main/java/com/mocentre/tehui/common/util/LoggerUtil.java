/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.util;

import org.apache.log4j.Logger;

/**
 * 类LoggerUtil.java的实现描述：日志
 * 
 * @author sz.gong 2016年11月1日 上午11:08:50
 */
public class LoggerUtil {

    /**
     * 主要日志
     */
    public static final Logger mainLog     = Logger.getLogger(LoggerUtil.class);

    /**
     * 日志自定义输出
     */
    public static final Logger tehuiwebLog = Logger.getLogger("tehuiweb");

}
