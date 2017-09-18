package com.mocentre.tehui.goods.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.CategoryManageService;
import com.mocentre.tehui.backend.model.CategoryInstance;
import com.mocentre.tehui.backend.param.CategoryParam;
import com.mocentre.tehui.backend.param.CategoryQueryParam;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;

/**
 * 分类表 Created by 王雪莹 on 2016/11/21.
 */
@Controller
@RequestMapping("/goods/category")
public class CategoryController extends BaseController {

    @Autowired
    private CategoryManageService categoryManageService;

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
    public String edit(HttpServletRequest request, HttpServletResponse response, Model model) {
        String idStr = request.getParameter("id");
        try {
            PlainResult<CategoryInstance> pr = categoryManageService.getCategoryById(Long.parseLong(idStr),
                    generageRequestId());
            if (pr.isSuccess()) {
                CategoryInstance category = pr.getData();
                model.addAttribute("category", category);
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
     * 获取店铺下所有信息
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "getPage.htm", method = RequestMethod.POST)
    public void getPage(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<CategoryInstance> lr = new ListJsonResult<CategoryInstance>();
        try {
            if (lr.isSuccess()) {
                CategoryQueryParam queryParam = super.bindDTParamClass(request, CategoryQueryParam.class);
                lr = categoryManageService.getCategoryPage(queryParam);
            }
        } catch (Exception e) {
            LOGGER.error("getPage", e);
            lr.setErrorMessage("999", "系统异常");
        }
        printJson(response, lr.toJsonString());
    }

    /**
     * 添加一个新分类
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "addCategory.htm", method = RequestMethod.POST)
    public void addCategory(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<Long> pr = new PlainResult<Long>();
        try {
            CategoryParam categoryParam = bindClass(request, CategoryParam.class);
            pr = categoryManageService.addCategory(categoryParam, generageRequestId());
        } catch (Exception e) {
            LOGGER.error("addCategory", e);
            pr.setErrorMessage("999", "系统异常");
        }
        printJson(response, pr.toJsonString());
    }

    @RequestMapping(value = "delByIdList.htm", method = RequestMethod.POST)
    public void delByIdList(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            String ids = request.getParameter("ids");
            List<Long> idList = JSON.parseArray(ids, Long.class);
            br = categoryManageService.delCategoryList(idList, generageRequestId());
        } catch (Exception e) {
            LOGGER.error("delById", e);
            br.setErrorMessage("999", "系统异常");
        }
        printJson(response, br.toJsonString());
    }

    /**
     * 修改 category
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "editCategory.htm", method = RequestMethod.POST)
    public void editCategory(HttpServletRequest request, HttpServletResponse response) {
        BaseResult baseResult = new BaseResult();
        Object shopid = request.getSession().getAttribute(SessionKeyConstant.SHOP);
        try {
            if (baseResult.isSuccess()) {
                CategoryParam param = super.bindClass(request, CategoryParam.class);
                param.setShopId((Long) shopid);
                baseResult = categoryManageService.editCategory(param);
            }
        } catch (Exception e) {
            LOGGER.error("addCategory", e);
            baseResult.setErrorMessage("999", "系统异常");
        }
        printJson(response, baseResult.toJsonString());
    }

    /**
     * 获取商品分类
     */
    @RequestMapping(value = "ajax/getCategory.htm", method = RequestMethod.POST)
    public void getCategory(HttpServletRequest request, HttpServletResponse response) {
        ListResult<CategoryInstance> lr = new ListResult<CategoryInstance>();
        try {
            lr = categoryManageService.getAllCategoryList(generageRequestId());
        } catch (Exception e) {
            lr.setErrorMessage("999", "系统异常");
            LOGGER.error("getCategory", e);
        }
        super.printJson(response, lr.toJsonString());
    }
}
