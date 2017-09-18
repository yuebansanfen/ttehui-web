package com.mocentre.tehui.goods.controller;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.SupplierManageService;
import com.mocentre.tehui.backend.model.SupplierInstance;
import com.mocentre.tehui.backend.param.SupplierParam;
import com.mocentre.tehui.backend.param.SupplierQueryParam;
import com.mocentre.tehui.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 供应商controller Created by yukaiji on 2017/8/30.
 */
@Controller
@RequestMapping("/goods/supplier")
public class SupplierController extends BaseController {

    @Autowired
    private SupplierManageService supplierManageService;

    /**
     * 跳转到首页
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
     * @return
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.GET)
    public String add() {
        return getNameSpace() + "add";
    }

    /**
     * 跳转到修改页面
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "edit.htm", method = RequestMethod.GET)
    public String edit(Long id, Model model) {
        try {
            PlainResult<SupplierInstance> pr = supplierManageService.SupplierEdit(id, generageRequestId());
            if (pr.isSuccess()) {
                SupplierInstance result = pr.getData();
                model.addAttribute("supplier", result);
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
     * 添加
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "addSupplier.htm", method = RequestMethod.POST)
    public void addDiscover(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        try {
            SupplierParam supplierParam = bindClass(request, SupplierParam.class);
            br = supplierManageService.addSupplier(supplierParam);
        } catch (Exception e) {
            LOGGER.error("addSupplier", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 修改
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "editSupplier.htm", method = RequestMethod.POST)
    public void editDiscover(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            SupplierParam supplierParam = bindClass(request, SupplierParam.class);
            br = supplierManageService.updateSupplier(supplierParam);
        } catch (Exception e) {
            LOGGER.error("addSupplier", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 分页查询
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "query.htm", method = RequestMethod.POST)
    public void query(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<SupplierInstance> br = new ListJsonResult<SupplierInstance>();
        try {
            if (br.isSuccess()) {
                SupplierQueryParam supplierQueryParam = bindDTParamClass(request, SupplierQueryParam.class);
                supplierQueryParam.setRequestId(generageRequestId());
                br = supplierManageService.querySupplierPage(supplierQueryParam);
            }
            super.printJson(response, br.toJsonString());
        } catch (Exception e) {
            LOGGER.error("query", e);
        }
    }

    /**
     * 删除
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        try {
            if (StringUtils.isBlank(id)) {
                br.setErrorMessage("1001", "参数错误");
            }
            if (br.isSuccess()) {
                br = supplierManageService.deleteSupplier(Long.valueOf(id), generageRequestId());
            }
            super.printJson(response, br.toJsonString());
        } catch (Exception e) {
            LOGGER.error("delete", e);
        }
        super.printJson(response, br.toJsonString());
    }

}
