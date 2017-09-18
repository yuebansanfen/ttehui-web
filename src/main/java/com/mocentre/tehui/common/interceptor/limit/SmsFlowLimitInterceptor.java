/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.interceptor.limit;

/**
 * 类SmsFlowLimitInterceptor.java的实现描述：发送短信拦截器
 * 
 * @author sz.gong 2017年8月17日 下午1:27:36
 */
public class SmsFlowLimitInterceptor extends AbstractSpringMonitor {

    private FlowMonitor flowMonitor;

    public SmsFlowLimitInterceptor(int flowSize) {
        flowMonitor = new FlowMonitor(flowSize);
    }

    @Override
    public boolean before() {
        return flowMonitor.entry();
    }

    @Override
    public boolean after() {
        flowMonitor.release();
        return true;
    }

}
