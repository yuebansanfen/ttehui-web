package com.mocentre.tehui.core.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Entity基类
 */
public class BaseEntity implements Serializable {

    private static final long  serialVersionUID = 2734951563954109759L;

    public static final String PROPERTY_ID      = "id";
    public static final String GMT_CREATED      = "gmtCreated";
    public static final String GMT_MODIFIED     = "gmtModified";
    public static final String IS_DELETED       = "isDeleted";

    private Long               id;                                     // 唯一标识
    private Date               gmtCreated;                             // 创建时间
    private Date               gmtModified;                            // 更新时间
    private Integer            isDeleted;                              // 有效性

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

    //    public int getStart() {
    //        return start;
    //    }
    //
    //    public void setStart(int start) {
    //        this.start = start;
    //    }
    //
    //    public int getLimit() {
    //        return limit;
    //    }
    //
    //    public void setLimit(int limit) {
    //        this.limit = limit;
    //    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

}
