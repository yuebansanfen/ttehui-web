/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.fee.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.AreasManageService;
import com.mocentre.tehui.FreightMouldManageService;
import com.mocentre.tehui.backend.model.FreightMouldAllMsgInstance;
import com.mocentre.tehui.backend.model.FreightMouldInstance;
import com.mocentre.tehui.backend.param.CarryModeParam;
import com.mocentre.tehui.backend.param.FreightMouldParam;
import com.mocentre.tehui.backend.param.FreightMouldQueryParam;
import com.mocentre.tehui.common.util.CommUtil;

/**
 * 类FreightMouldService.java的实现描述：运费service
 * 
 * @author yukaiji 2016年11月14日
 */
@Component
public class FreightMouldService {

	@Autowired
	private FreightMouldManageService freightMouldManageService;
	@Autowired
	private AreasManageService areasMouldManageService;

	public ListJsonResult<FreightMouldInstance> queryFreightMouldPage(FreightMouldQueryParam freightMouldQuery,
			Object shopId) {
		ListJsonResult<FreightMouldInstance> br = new ListJsonResult<FreightMouldInstance>();
		freightMouldQuery.setShopId(Long.valueOf(shopId.toString()));
		ListJsonResult<FreightMouldInstance> result = freightMouldManageService
				.queryFreightMouldPage(freightMouldQuery);
		if (result != null) {
			if (result.isSuccess()) {
				br = result;
			}
		}
		return br;
	}

	public BaseResult addFreightMould(Object shopId, String freightParam, String carryModeListParam) {
		BaseResult br = new BaseResult();
		List<CarryModeParam> carryModeParamList = new ArrayList<CarryModeParam>();
		JSONObject freObj = JSON.parseObject(freightParam);
		JSONArray carryModeArray = JSON.parseArray(carryModeListParam);
		FreightMouldParam freParam = JSON.toJavaObject(freObj, FreightMouldParam.class);
		freParam.setShopId(Long.valueOf(shopId.toString()));
		for (int i = 0; i < carryModeArray.size(); i++) {
			JSONObject jobj = carryModeArray.getJSONObject(i);
			CarryModeParam o = JSON.toJavaObject(jobj, CarryModeParam.class);
			carryModeParamList.add(o);
		}
		BaseResult result = freightMouldManageService.addFreightMould(freParam, carryModeParamList);
		if (result != null) {
			if (!result.isSuccess()) {
				br.setErrorMessage("100", "添加失败");
			} else {
				br = result;
			}
		} else {
			br.setErrorMessage("100", "添加失败");
		}
		return br;
	}

	public BaseResult editFreightMould(Object shopId, String freightParam, String carryModeListParam) {
		BaseResult br = new BaseResult();
		List<CarryModeParam> carryModeParamList = new ArrayList<CarryModeParam>();
		JSONObject freObj = JSON.parseObject(freightParam);
		JSONArray carryModeArray = JSON.parseArray(carryModeListParam);
		FreightMouldParam freParam = JSON.toJavaObject(freObj, FreightMouldParam.class);
		freParam.setShopId(Long.valueOf(shopId.toString()));
		for (int i = 0; i < carryModeArray.size(); i++) {
			JSONObject jobj = carryModeArray.getJSONObject(i);
			CarryModeParam o = JSON.toJavaObject(jobj, CarryModeParam.class);
			carryModeParamList.add(o);
		}
		BaseResult result = freightMouldManageService.editFreightMould(freParam, carryModeParamList);
		if (result != null) {
			if (!result.isSuccess()) {
				br.setErrorMessage("100", "修改失败");
			} else {
				br = result;
			}
		} else {
			br.setErrorMessage("100", "修改失败");
		}
		return br;
	}

	public BaseResult delete(String idList) {
		BaseResult br = new BaseResult();
		String requestId = CommUtil.generateUUID();

		// idList字符串转List<Long>
		String[] sIdList = idList.split(",");
		List<Long> lIdList = new ArrayList<Long>();
		for (String id : sIdList) {
			lIdList.add(Long.valueOf(id));
		}

		BaseResult result = freightMouldManageService.deleteFreightMould(lIdList, requestId);
		br.setRequestId(requestId);
		if (result != null) {
			if (!result.isSuccess()) {
				br.setErrorMessage("100", "删除失败");
			}
		} else {
			br.setErrorMessage("100", "删除失败");
		}
		return br;
	}

	public String getResult() {
		String result = areasMouldManageService.getAllProvinceTwoLinkage();
		return result;
	}

	public PlainResult<FreightMouldAllMsgInstance> getFreightMouldById(Long id, Object shopId) {
		String requestId = CommUtil.generateUUID();
		PlainResult<FreightMouldAllMsgInstance> pr = new PlainResult<>();
		PlainResult<FreightMouldAllMsgInstance> result = freightMouldManageService.getFreightMouldById(id, Long.valueOf(shopId.toString()), requestId);
		if (result != null) {
			if (result.isSuccess()) {
				pr = result;
			}
		}
		return pr;
	}

}
