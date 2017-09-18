package com.mocentre.tehui.wechat.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.ActivityAllMsgFTInstance;
import com.mocentre.tehui.frontend.model.GrouponFTInstance;
import com.mocentre.tehui.frontend.service.ActivityManageService;
import com.mocentre.tehui.frontend.service.GrouponManageService;

/**
 * 微信活动接口 Created by yukaiji on 2016/01/22.
 */

@Controller
@RequestMapping("/wxa/activity")
public class ActivityWXController extends BaseController {

    @Autowired
    private ActivityManageService activityManageService;
    @Autowired
    private GrouponManageService  grouponManageService;

    /**
     * 根据店铺id和活动id获取
     */
    @RequestMapping(value = "getGoodsDetail.htm", method = RequestMethod.POST)
    public void getGoodsDetail(HttpServletRequest request, HttpServletResponse response) {
        String shopId = request.getParameter("shopId");
        String activityId = request.getParameter("activityId");
        PlainResult<ActivityAllMsgFTInstance> result = new PlainResult<>();
        try {
            if (StringUtils.isBlank(shopId)) {
                result.setErrorMessage("1001", "店铺id为空");
            }
            if (StringUtils.isBlank(activityId)) {
                result.setErrorMessage("1001", "活动id为空");
            }
            if (result.isSuccess()) {
                result = activityManageService.queryActivityWithGoods(Long.valueOf(shopId), Long.valueOf(activityId),
                        generageRequestId());
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 根据活动类型获取活动列表(首页展示)
     */
    @RequestMapping(value = "getActivityIndex.htm", method = RequestMethod.POST)
    public void getActivityIndex(HttpServletRequest request, HttpServletResponse response) {
        String actType = request.getParameter("actType");
        ListResult<ActivityAllMsgFTInstance> result = new ListResult<>();
        try {
            if (StringUtils.isBlank(actType)) {
                result.setErrorMessage("1001", "活动类型为空");
            }
            if (result.isSuccess()) {
                result = activityManageService.queryActivityIndex(actType, generageRequestId());
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 获取所有未完成的团
     */
    @RequestMapping(value = "getUnfinished.htm", method = RequestMethod.GET)
    public void getUnfinished(HttpServletRequest request, HttpServletResponse response) {
        String actGoodsId = request.getParameter("actGoodsId");
        ListResult<GrouponFTInstance> result = new ListResult<GrouponFTInstance>();
        try {
            result = grouponManageService.queryGrouponByUnfinished(actGoodsId, generageRequestId());
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, JSON.toJSONString(result));
    }
}
