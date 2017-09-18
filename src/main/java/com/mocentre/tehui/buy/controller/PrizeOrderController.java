package com.mocentre.tehui.buy.controller;

import java.util.List;
import java.util.Map;

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
import com.mocentre.tehui.PrizeOrderManageService;
import com.mocentre.tehui.backend.model.LogisticsInstance;
import com.mocentre.tehui.backend.model.PrizeOrderInstance;
import com.mocentre.tehui.backend.param.PrizeOrderParam;
import com.mocentre.tehui.backend.param.PrizeOrderQueryParam;
import com.mocentre.tehui.core.controller.BaseController;

/**
 * 实物礼品订单controller Created by wangxueying on 2017/8/11.
 */
@Controller
@RequestMapping("/buy/prizeOrder")
public class PrizeOrderController extends BaseController {

    @Autowired
    private PrizeOrderManageService prizeOrderManageService;

    /**
     * 跳转到首页
     */
    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response) {
        return getNameSpace() + "index";
    }

    /**
     * 跳转到详情
     */
    @RequestMapping(value = "detail.htm", method = RequestMethod.GET)
    public String detail(Long id, Model model, HttpServletRequest request, HttpServletResponse response) {
        try {
            PlainResult<Map<String, Object>> pr = prizeOrderManageService.getById(id, generageRequestId());
            if (pr.isSuccess()) {
                Map<String, Object> data = pr.getData();
                PrizeOrderInstance prizeOrder = (PrizeOrderInstance) data.get("prizeOrder");
                List<LogisticsInstance> logisticsList = (List<LogisticsInstance>) data.get("logisticsList");
                model.addAttribute("prizeOrder", prizeOrder);
                model.addAttribute("logisticsList", logisticsList);
            } else {
                model.addAttribute("errorMsg", "访问失败");
                return getErrorIndex();
            }
        } catch (Exception e) {
            LOGGER.error("edit", e);
            model.addAttribute("errorMsg", "系统异常");
            return getErrorIndex();
        }
        return getNameSpace() + "detail";
    }

    /**
     * 分页查询
     */
    @RequestMapping(value = "query.htm", method = RequestMethod.POST)
    public void query(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<PrizeOrderInstance> br = new ListJsonResult<PrizeOrderInstance>();
        try {
            PrizeOrderQueryParam queryParam = super.bindDTParamClass(request, PrizeOrderQueryParam.class);
            br = prizeOrderManageService.queryPrizeOrderPage(queryParam);
        } catch (Exception e) {
            LOGGER.error("query", e);
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 修改
     */
    @RequestMapping(value = "editPrizeOrder.htm", method = RequestMethod.POST)
    public void editPrizeOrder(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            PrizeOrderParam param = super.bindClass(request, PrizeOrderParam.class);
            param.setRequestId(generageRequestId());
            br = prizeOrderManageService.updatePrizeOrder(param);
        } catch (Exception e) {
            LOGGER.error("editPrizeOrder", e);
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }
}
