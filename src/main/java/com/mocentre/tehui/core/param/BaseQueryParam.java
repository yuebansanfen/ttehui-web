/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.core.param;

/**
 * 类BaseQueryParam.java的实现描述：datatables查询参数
 * 
 * @author sz.gong 2016年4月28日 下午2:34:01
 */
public class BaseQueryParam extends BaseParam {

    private static final long serialVersionUID = 1L;

    /** 排序字段 **/
    private String            orderColumn;

    /** 排序 **/
    private String            orderBy;

    /** 开始序列 **/
    private Integer           start;

    /** 显示条数 **/
    private Integer           length;

    private Integer           draw;

    public String getOrderColumn() {
        return orderColumn;
    }

    public void setOrderColumn(String orderColumn) {
        this.orderColumn = orderColumn;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

}
