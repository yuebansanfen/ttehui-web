/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.fee.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.backend.model.FreightMouldAllMsgInstance;
import com.mocentre.tehui.backend.model.FreightMouldInstance;
import com.mocentre.tehui.backend.param.FreightMouldQueryParam;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.fee.service.FreightMouldService;

/**
 * 类FreightMouldController.java的实现描述：对运费模板进行操作controller
 * 
 * @author YuKaiji 201611月08日 10:00:58
 */
@Controller
@RequestMapping("/fee/freightmould")
public class FreightMouldController extends BaseController {

	@Autowired
	private FreightMouldService freightMouldService;

	/**
	 * 主页
	 * 
	 * @return
	 */
	@RequestMapping(value = "index.htm", method = RequestMethod.GET)
	public String index() {
		return getNameSpace() + "index";
	}

	/**
	 * 跳转到添加页面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "add.htm", method = RequestMethod.GET)
	public String add(Model model) {
		try {
			String cityInfo = freightMouldService.getResult();
			model.addAttribute("cityInfo", cityInfo);
		} catch (Exception e) {
			LOGGER.error("query", e);
		}
		return getNameSpace() + "add";
	}

	/**
	 * 跳转到修改页面
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "edit.htm", method = RequestMethod.GET)
	public String edit(Long id, Model model, HttpServletRequest request) {
		// 获取模板基本信息和对应的运送方式信息 ,返回给前台
		PlainResult<FreightMouldAllMsgInstance> result = new PlainResult<FreightMouldAllMsgInstance>();
		try {
			Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
			if (shopId == null) {
				result.setErrorMessage("1001", "店铺id为空");
			}
			if (result.isSuccess()) {
				result = freightMouldService.getFreightMouldById(id, shopId);
				String cityInfo = freightMouldService.getResult();
				model.addAttribute("cityInfo", cityInfo);
				model.addAttribute("result", result.toJsonString());
			}
		} catch (Exception e) {
			LOGGER.error("query", e);
		}
		return getNameSpace() + "edit";
	}

	/**
	 * 查询所有模板
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "query.htm", method = RequestMethod.POST)
	public void query(HttpServletRequest request, HttpServletResponse response) {
		ListJsonResult<FreightMouldInstance> br = new ListJsonResult<FreightMouldInstance>();
		Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
		if (shopId == null) {
			br.setErrorMessage("1001", "店铺id为空");
		}
		try {
			if (br.isSuccess()) {
				FreightMouldQueryParam freightMouldQueryParam = bindClass(request, FreightMouldQueryParam.class);
				br = freightMouldService.queryFreightMouldPage(freightMouldQueryParam, shopId);
			}
			super.printJson(response, br.toJsonString());
		} catch (Exception e) {
			LOGGER.error("query", e);
		}
	}

	/**
	 * 添加模板信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "addFreightMould.htm", method = RequestMethod.POST)
	public void addFreightMould(HttpServletRequest request, HttpServletResponse response) {
		BaseResult br = new BaseResult();
		try {
			Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
			String freightParam = request.getParameter("freightMould");
			String carryModeListParam = request.getParameter("carryModeList");
			if (shopId != null) {
				br = freightMouldService.addFreightMould(shopId, freightParam, carryModeListParam);
			} else {
				br.setErrorMessage("100", "添加失败");
			}
		} catch (Exception e) {
			LOGGER.error("addFreightMould", e);
		}
		super.printJson(response, br.toJsonString());
	}

	/**
	 * 修改模板信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "editFreightMould.htm", method = RequestMethod.POST)
	public void editFreightMould(HttpServletRequest request, HttpServletResponse response) {
		BaseResult br = new BaseResult();
		try {
			Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
			String freightParam = request.getParameter("freightMould");
			String carryModeListParam = request.getParameter("carryModeList");
			if (shopId != null) {
				br = freightMouldService.editFreightMould(shopId, freightParam, carryModeListParam);
			} else {
				br.setErrorMessage("100", "修改失败");
			}
		} catch (Exception e) {
			LOGGER.error("editFreightMould", e);
		}
		super.printJson(response, br.toJsonString());
	}

	/**
	 * 删除一个模板
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "delete.htm", method = RequestMethod.POST)
	public void deleteFreightMould(HttpServletRequest request, HttpServletResponse response) {
		BaseResult br = new BaseResult();
		try {
			String idList = request.getParameter("idList");
			br = freightMouldService.delete(idList);
		} catch (Exception e) {
			LOGGER.error("delete", e);
		}
		super.printJson(response, br.toJsonString());
	}
}
