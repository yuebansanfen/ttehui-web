package com.mocentre.tehui.front.controller;

import com.alibaba.fastjson.JSON;
import com.mocentre.common.ListResult;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.GrouponFTInstance;
import com.mocentre.tehui.frontend.service.GrouponManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拼团活动后台Controller
 * 
 * @author Created by yukaiji on 2016年12月27日
 */
@Controller
@RequestMapping("/front/groupon")
public class GrouponFTController extends BaseController {

    @Autowired
    private GrouponManageService grouponManageService;

    /**
     * 获取所有未完成的团
     */
    @RequestMapping(value = "getUnfinished.htm", method = RequestMethod.POST)
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
