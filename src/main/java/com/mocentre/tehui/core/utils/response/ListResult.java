/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.core.utils.response;

import java.util.List;

/**
 * 类ListResult.java的实现描述：用于接口返回list
 * 
 * @author sz.gong 2016年4月29日 上午11:36:59
 */
public class ListResult<T> extends BaseResult {

    private static final long serialVersionUID = 1L;

    private Integer           count;

    private List<T>           data;

    public ListResult() {

    }

    public ListResult(List<T> data, Integer count) {
        this.count = count == null ? data.size() : count;
        this.data = data;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

}
