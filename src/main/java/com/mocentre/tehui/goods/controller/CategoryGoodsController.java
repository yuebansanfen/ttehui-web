package com.mocentre.tehui.goods.controller;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.MapResult;
import com.mocentre.tehui.CategoryGoodsManageService;
import com.mocentre.tehui.backend.model.CategoryGoodsInstance;
import com.mocentre.tehui.backend.param.CategoryGoodsParam;
import com.mocentre.tehui.backend.param.CategoryGoodsQueryParam;
import com.mocentre.tehui.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 分类商品管理 Created by yukaiji on 2017/7/4.
 */
@Controller
@RequestMapping("/goods/categoryGoods")
public class CategoryGoodsController extends BaseController {

    @Autowired
    private CategoryGoodsManageService categoryGoodsManageService;

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
            MapResult result = categoryGoodsManageService.getCategoryGoodsById(Long.valueOf(idStr),
                    generageRequestId());
            if (result.isSuccess()) {
                Map<String, Object> info = result.getData();
                for (String key : info.keySet()) {
                    model.addAttribute(key, info.get(key));
                }
            } else {
                model.addAttribute("errorMsg", "访问失败");
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
    @RequestMapping(value = "query.htm", method = RequestMethod.POST)
    public void query(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<CategoryGoodsInstance> lr = new ListJsonResult<CategoryGoodsInstance>();
        try {
            if (lr.isSuccess()) {
                CategoryGoodsQueryParam queryParam = super.bindDTParamClass(request, CategoryGoodsQueryParam.class);
                lr = categoryGoodsManageService.queryCategoryGoodsPage(queryParam);
            }
        } catch (Exception e) {
            LOGGER.error("query", e);
            lr.setErrorMessage("999", "系统异常");
        }
        printJson(response, lr.toJsonString());
    }

    /**
     * 添加
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "addCategoryGoods.htm", method = RequestMethod.POST)
    public void addCategory(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            CategoryGoodsParam categoryGoodsParam = bindClass(request, CategoryGoodsParam.class);
            br = categoryGoodsManageService.addCategoryGoods(categoryGoodsParam);
        } catch (Exception e) {
            LOGGER.error("addCategoryGoods", e);
            br.setErrorMessage("999", "系统异常");
        }
        printJson(response, br.toJsonString());
    }

    /**
     * 修改
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "editCategoryGoods.htm", method = RequestMethod.POST)
    public void editCategory(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            if (br.isSuccess()) {
                CategoryGoodsParam param = super.bindClass(request, CategoryGoodsParam.class);
                br = categoryGoodsManageService.updateCategoryGoods(param);
            }
        } catch (Exception e) {
            LOGGER.error("editCategoryGoods", e);
            br.setErrorMessage("999", "系统异常");
        }
        printJson(response, br.toJsonString());
    }

    /**
     * 删除
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "delById.htm", method = RequestMethod.POST)
    public void delById(HttpServletRequest request, HttpServletResponse response) {
        String idStr = request.getParameter("id");
        BaseResult br = new BaseResult();
        try {
            br = categoryGoodsManageService.deleteCategoryGoodsById(Long.valueOf(idStr), generageRequestId());
        } catch (Exception e) {
            LOGGER.error("delById", e);
            br.setErrorMessage("999", "系统异常");
        }
        printJson(response, br.toJsonString());
    }

}
