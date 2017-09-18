package com.mocentre.tehui.core.utils.response;

/**
 * 类PlainResult.java的实现描述：TODO 类实现描述
 * 
 * @author Arvin 2015年3月14日 下午11:10:11
 */
public class PlainResult<T> extends BaseResult {

    /**
     * 
     */
    private static final long serialVersionUID = 7083406773571014850L;
    private T                 data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
