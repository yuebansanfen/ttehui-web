package com.mocentre.tehui.goods.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.AttributeManageService;
import com.mocentre.tehui.backend.model.AttributeInstance;
import com.mocentre.tehui.backend.param.AttributeParam;
import com.mocentre.tehui.backend.param.AttributeQueryParam;
import com.mocentre.tehui.core.controller.BaseController;

/**
 * 属性controller Created by 王雪莹 on 2016/11/8.
 */
@Controller
@RequestMapping("/goods/attribute")
public class AttributeController extends BaseController {

    @Autowired
    private AttributeManageService attributeService;

    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response) {
        return getNameSpace() + "index";
    }

    /**
     * 获取所有
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "getPage.htm", method = RequestMethod.POST)
    public void getPage(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<AttributeInstance> ljr = new ListJsonResult<AttributeInstance>();
        try {
            AttributeQueryParam queryParam = bindDTParamClass(request, AttributeQueryParam.class);
            ljr = attributeService.getPage(queryParam);
        } catch (Exception e) {
            LOGGER.error("getPage", e);
        }
        super.printJson(response, ljr.toJsonString());
    }

    /**
     * 新加一个属性
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "addAttribute.htm", method = RequestMethod.POST)
    public void addAttribute(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String isImg = request.getParameter("isImg");
        try {
            AttributeParam attrParam = super.bindClass(request, AttributeParam.class);
            if (StringUtils.isNotBlank(isImg) && "1".equals(isImg)) {
                attrParam.setNeedImg(true);
            } else {
                attrParam.setNeedImg(false);
            }
            br = attributeService.addAttribute(attrParam);
        } catch (Exception e) {
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
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
    public String edit(Long id, HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            PlainResult<AttributeInstance> pr = attributeService.getById(id, generageRequestId());
            if (pr.isSuccess()) {
                model.addAttribute("attribute", pr.getData());
            } else {
                model.addAttribute("errorMsg", pr.getMessage());
                return getErrorIndex();
            }
        } catch (Exception e) {
            LOGGER.error("edit", e);
            model.addAttribute("errorMsg", "系统异常");
            return getErrorIndex();
        }
        return getNameSpace() + "edit";
    }

    /**
     * 修改一个属性
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "editAttribute.htm", method = RequestMethod.POST)
    public void editAttribute(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        String isImg = request.getParameter("isImg");
        try {
            AttributeParam attrParam = super.bindClass(request, AttributeParam.class);
            if (StringUtils.isNotBlank(isImg) && "1".equals(isImg)) {
                attrParam.setNeedImg(true);
            } else {
                attrParam.setNeedImg(false);
            }
            if (StringUtils.isNotBlank(id)) {
                br = attributeService.update(Long.parseLong(id), attrParam);
            } else {
                br.setErrorMessage("1001", "id为空");
            }
        } catch (Exception e) {
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 批量删除
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "delAttributeList.htm", method = RequestMethod.POST)
    public void delAttributeList(HttpServletRequest request, HttpServletResponse response) {
        String ids = request.getParameter("ids");
        List<Long> idList = JSON.parseArray(ids, Long.class);
        BaseResult br = attributeService.del(idList, generageRequestId());
        super.printJson(response, br.toJsonString());
    }

}
