/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.interceptor.limit;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 类FlowMonitor.java的实现描述：限流作用
 * 
 * @author sz.gong 2017年8月17日 上午10:24:02
 */
public class FlowMonitor {

    //最大并发数。默认为100，可配置
    private int           maxFlowSize    = 100;

    //最大并发数  
    private int           maxRunningSize = 0;

    //当前并发数
    private AtomicInteger runningSize    = new AtomicInteger();

    //通过的数量  
    //private AtomicInteger passSize       = new AtomicInteger();

    //失败的数量  
    //private AtomicInteger loseSize       = new AtomicInteger();

    public FlowMonitor() {
        super();
    }

    public FlowMonitor(int maxFlowSize) {
        super();
        if (maxFlowSize > 0) {
            this.maxFlowSize = maxFlowSize;
        } else {
            throw new RuntimeException("maxFlowSize must greater than zero");
        }
    }

    /**
     * 线程进入，并发数+1
     * 
     * @return
     */
    public boolean entry() {
        int curRunningSize = runningSize.incrementAndGet();
        if (curRunningSize > maxFlowSize) {//超过最大限制
            runningSize.set(maxFlowSize);
            //loseSize.incrementAndGet();
            return false;
        }

        if (runningSize.get() > maxRunningSize) {
            maxRunningSize = runningSize.get();
        }
        //passSize.incrementAndGet();
        return true;
    }

    /**
     * 执行完后，并发数-1
     */
    public void release() {
        runningSize.decrementAndGet();
    }

}
