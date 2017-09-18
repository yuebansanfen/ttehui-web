/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.interceptor.limit;

/**
 * 类MonitorHandler.java的实现描述：抽象接口
 * 
 * @author sz.gong 2017年8月17日 上午10:49:11
 */
public interface MonitorHandler {

    public boolean before();

    public boolean after();

}
