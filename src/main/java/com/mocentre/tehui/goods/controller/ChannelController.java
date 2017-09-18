package com.mocentre.tehui.goods.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.mocentre.tehui.ChannelManageService;
import com.mocentre.tehui.backend.param.ChannelParam;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.core.utils.response.PlainResult;

/**
 * 频道表 Created by 王雪莹 on 2016/11/21.
 */
@Controller
@RequestMapping("/goods/channel")
public class ChannelController extends BaseController {

    @Autowired
    private ChannelManageService channelManageService;

    /**
     * 跳转初始化页面
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response) {
        return getNameSpace() + "index";
    }

    /**
     * 跳转属性添加页面
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.GET)
    public String add(HttpServletRequest request, HttpServletResponse response) {
        return getNameSpace() + "add";
    }

    /**
     * 跳转属性修改页面
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "edit.htm", method = RequestMethod.GET)
    public String edit(HttpServletRequest request, HttpServletResponse response) {
        return getNameSpace() + "edit";
    }

    /**
     * 获取店铺下所有信息
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "getAll.htm", method = RequestMethod.POST)
    public void getAll(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<String> pr = new PlainResult();
        try {
            Object obj = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            Long shopId = Long.valueOf(obj.toString());
            String channelByShopId = channelManageService.getChannelByShopId(shopId);
            pr.setData(channelByShopId);
            printJson(response, pr.toJsonString());
            System.out.println("------------........." + pr.toJsonString());
        } catch (Exception e) {
            LOGGER.error("getAll", e);
            pr.setErrorMessage("999", "系统异常");
            printJson(response, pr.toJsonString());
        }
    }

    /**
     * 添加一个新频道
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "addChannel.htm", method = RequestMethod.POST)
    public void addChannel(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<String> pr = new PlainResult<String>();
        try {
            Object obj = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            Long shopId = Long.valueOf(obj.toString());
            ChannelParam param = bindClass(request, ChannelParam.class);
            param.setShopId(shopId);
            Long i = channelManageService.addChannel(param);
        } catch (Exception e) {
            LOGGER.error("addChannel", e);
            pr.setErrorMessage("999", "系统异常");
        }
        printJson(response, pr.toJsonString());
    }

    /**
     * 根据id获取信息
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "getById.htm", method = RequestMethod.POST)
    public void getById(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        PlainResult<String> pr = new PlainResult();
        try {
            if (!StringUtils.isEmpty(id)) {
                long idl = Long.parseLong(id);
                String channelById = channelManageService.getChannelById(idl);
                pr.setData(channelById);
                printJson(response, pr.toJsonString());
            } else {
                pr.setErrorMessage("999", "查询失败");
                printJson(response, pr.toJsonString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("getById", e);
            pr.setErrorMessage("999", "系统异常");
            printJson(response, pr.toJsonString());
        }
    }

    /**
     * 根据id获取信息
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "delById.htm", method = RequestMethod.POST)
    public void delById(Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            Object obj = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            Long shopId = Long.valueOf(obj.toString());
            Long i = channelManageService.delChannel(id, shopId);
            System.out.println(i + "-------------------------------" + "delById()");
            printJson(response, "success");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("delById", e);
            printJson(response, "系统异常");
        }
    }
}
