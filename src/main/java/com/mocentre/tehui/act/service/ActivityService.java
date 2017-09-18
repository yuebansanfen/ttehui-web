/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.act.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.ActivityManageService;
import com.mocentre.tehui.GrouponManageService;
import com.mocentre.tehui.backend.model.ActivityAllMsgInstance;
import com.mocentre.tehui.backend.model.ActivityGoodsInstance;
import com.mocentre.tehui.backend.model.ActivityInstance;
import com.mocentre.tehui.backend.model.GrouponInstance;
import com.mocentre.tehui.backend.param.ActivityGoodsParam;
import com.mocentre.tehui.backend.param.ActivityGoodsQueryParam;
import com.mocentre.tehui.backend.param.ActivityParam;
import com.mocentre.tehui.backend.param.ActivityQueryParam;
import com.mocentre.tehui.backend.param.GrouponQueryParam;

/**
 * 类SubjectService.java的实现描述：专题service
 * 
 * @author yukaiji 2016年12月03日
 */
@Component
public class ActivityService {

    @Autowired
    private ActivityManageService activityManageService;
    @Autowired
    private GrouponManageService  grouponManageService;

    /**
     * 根据条件查询活动列表
     */
    public ListResult<ActivityInstance> queryActivityListByParam(Long shopId, String type, String requestId) {
        return activityManageService.queryAbcActivity(shopId, type, requestId);
    }

    /**
     * 根据条件查询活动商品列表
     */
    public ListResult<ActivityGoodsInstance> queryActivityListByActivityId(Long activityId, String requestId) {
        return activityManageService.queryActivityGoodsByActivityId(activityId, requestId);
    }

    /**
     * 根据id查询活动商品
     */
    public PlainResult<ActivityGoodsInstance> queryActivityGoodsInfo(Long id, String requestId) {
        return activityManageService.queryActivityGoodsById(id, requestId);
    }

    /**
     * 分页查询所有的活动
     */
    public ListJsonResult<ActivityInstance> queryActivityPage(ActivityQueryParam activityQuery) {
        ListJsonResult<ActivityInstance> result = activityManageService.queryActivityPage(activityQuery);
        return result;
    }

    /**
     * 分页查询关联商品
     */
    public ListJsonResult<ActivityGoodsInstance> queryActivityGoodsPage(ActivityGoodsQueryParam actGoodsQueryParam) {
        ListJsonResult<ActivityGoodsInstance> result = activityManageService.queryActivityGoodsPage(actGoodsQueryParam);
        return result;
    }

    /**
     * 分页查询关联商品
     */
    public ListJsonResult<GrouponInstance> queryGrouponPage(GrouponQueryParam grouponQueryParam) {
        ListJsonResult<GrouponInstance> result = grouponManageService.queryGrouponPage(grouponQueryParam);
        return result;
    }

    /**
     * 活动修改页面
     */
    public PlainResult<ActivityInstance> activityEdit(Long id, Long shopId) {
        PlainResult<ActivityInstance> result = activityManageService.activityEdit(id, shopId);
        return result;
    }

    /**
     * 活动商品修改页面
     */
    public PlainResult<ActivityAllMsgInstance> activityGoodsEdit(Long actId, Long actGoodsId) {
        PlainResult<ActivityAllMsgInstance> result = activityManageService.activityGoodsEdit(actId, actGoodsId);
        return result;
    }

    /**
     * 添加活动
     */
    public BaseResult addActivity(ActivityParam actParam) {
        return activityManageService.addActivity(actParam);
    }

    /**
     * 修改活动
     */
    public BaseResult editActivity(ActivityParam actParam) {
        return activityManageService.updateActivity(actParam);
    }

    /**
     * 添加活动管理商品
     */
    public BaseResult addActivityGoods(ActivityGoodsParam actGoodsParam) {
        return activityManageService.addActivityGoods(actGoodsParam);
    }

    /**
     * 修改活动商品
     */
    public BaseResult editActivityGoods(ActivityGoodsParam actGoodsParam) {
        return activityManageService.updateActivityGoods(actGoodsParam);
    }

    /**
     * 更改展示状态
     */
    public BaseResult show(String id, String show) {
        BaseResult br = new BaseResult();
        br = activityManageService.show(id, show);
        return br;
    }

    /**
     * 活动删除(相应删除关联商品和库存)
     */
    public BaseResult delete(Long id, String requestId) {
        return activityManageService.deleteActivity(id, requestId);
    }

    /**
     * 活动商品删除
     */
    public BaseResult deleteGoods(Long id, String requestId) {
        return activityManageService.deleteActivityGoods(id, requestId);
    }

    /**
     * 批量修改活动起止时间
     */
    public BaseResult updateAllActGoodsTime(Long activityId, String beginTime, String endTime) {
        return activityManageService.updateAllActivityGoodsTime(activityId, beginTime, endTime);
    }

}
