package com.mocentre.tehui.front.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.PlainResult;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.ActivityAllMsgFTInstance;
import com.mocentre.tehui.frontend.service.ActivityManageService;

/**
 * 前台活动接口 Created by yukaiji on 2016/01/22.
 */

@Controller
@RequestMapping("/front/activity")
public class ActivityFTController extends BaseController {
	
	@Autowired
	private ActivityManageService activityManageService;
	
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

}
