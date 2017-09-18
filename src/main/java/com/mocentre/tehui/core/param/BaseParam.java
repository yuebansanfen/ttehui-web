/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.core.param;

import java.io.Serializable;
import java.util.Date;

/**
 * 类BaseParam.java的实现描述：参数
 * 
 * @author sz.gong 2016年4月27日 下午5:05:04
 */
public class BaseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    private Long              id;

    /**
     * 查询开始时间
     */
    private Date              gmtCreated;

    /**
     * 查询结束时间
     */
    private Date              gmtModified;

    /**
     * 请求唯一标示
     */
    private String            token;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
