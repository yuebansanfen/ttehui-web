package com.mocentre.tehui.core.utils.response;

import java.util.List;

/**
 * 类ListJsonResult.java的实现描述：用于支持jQuery dataTable json数据返回
 * 
 * @author sz.gong 2016年3月7日 下午6:03:46
 */
public class ListJsonResult<T> extends BaseResult {

    /**
     * 
     */
    private static final long serialVersionUID = 6936475727194962439L;

    /**
     * 操作次数
     */
    private int               draw;

    /**
     * 数据总数
     */
    private long              recordsTotal;

    /**
     * 过滤之后，实际的行数
     */
    private long              recordsFiltered;

    /**
     * list数据
     */
    private List<T>           data;

    public ListJsonResult() {
    }

    public ListJsonResult(int draw, long recordsTotal, long recordsFiltered, List<T> list) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.data = list;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

}
