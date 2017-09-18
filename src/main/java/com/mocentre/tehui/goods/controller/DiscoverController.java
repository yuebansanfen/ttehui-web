package com.mocentre.tehui.goods.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.backend.model.CategoryInstance;
import com.mocentre.tehui.backend.model.DiscoverAllMsgInstance;
import com.mocentre.tehui.backend.model.DiscoverInstance;
import com.mocentre.tehui.backend.param.DiscoverParam;
import com.mocentre.tehui.backend.param.DiscoverQueryParam;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.goods.service.DiscoverService;

/**
 * 发现页controller Created by yukaiji on 2016/11/17.
 */
@Controller
@RequestMapping("/goods/discover")
public class DiscoverController extends BaseController {

    @Autowired
    private DiscoverService discoverService;

    /**
     * 跳转到首页
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
     * 跳转到添加页面
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.GET)
    public String add(Model model, HttpServletRequest request, HttpServletResponse response) {
        List<CategoryInstance> cyList = discoverService.queryCategory();
        model.addAttribute("cyList", cyList);
        return getNameSpace() + "add";
    }

    /**
     * 跳转到修改页面
     * 
     * @param request
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "edit.htm", method = RequestMethod.GET)
    public String edit(Long id, Model model, HttpServletRequest request) {
        try {
            PlainResult<DiscoverAllMsgInstance> pr = discoverService.prviewEdit(id);
            if (pr.isSuccess()) {
                DiscoverAllMsgInstance resutl = pr.getData();
                model.addAttribute("discover", resutl.getDiscoverIns());
                model.addAttribute("cyList", resutl.getCyIns());
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
     * 添加配置页
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "addDiscover.htm", method = RequestMethod.POST)
    public void addDiscover(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        try {
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession()
                    .getServletContext());
            MultipartHttpServletRequest multiRequest = multipartResolver.resolveMultipart(request);
            Object shopid = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            if (shopid != null) {
                DiscoverParam discover = bindClass(multiRequest, DiscoverParam.class);
                discover.setShopId((Long) shopid);
                br = discoverService.discoverAdd(discover);
            } else {
                br.setErrorMessage("100", "添加失败");
            }
        } catch (Exception e) {
            LOGGER.error("addDiscover", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 修改配置页
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "editDiscover.htm", method = RequestMethod.POST)
    public void editDiscover(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        try {
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession()
                    .getServletContext());
            MultipartHttpServletRequest multiRequest = multipartResolver.resolveMultipart(request);
            Object shopid = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            if (shopid != null) {
                DiscoverParam discover = bindClass(multiRequest, DiscoverParam.class);
                discover.setShopId((Long) shopid);
                br = discoverService.discoverEdit(discover);
            } else {
                br.setErrorMessage("100", "修改失败");
            }
        } catch (Exception e) {
            LOGGER.error("editDiscover", e);
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 是否展示
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "updateshow.htm", method = RequestMethod.POST)
    public void show(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        String show = request.getParameter("show");
        try {
            br = discoverService.show(Long.parseLong(id), Integer.parseInt(show));
        } catch (Exception e) {
            LOGGER.error("show", e);
            br.setErrorMessage("999", "系统异常");
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
        ListJsonResult<DiscoverInstance> br = new ListJsonResult<DiscoverInstance>();
        try {
            if (br.isSuccess()) {
                DiscoverQueryParam discoverParam = bindDTParamClass(request, DiscoverQueryParam.class);
                discoverParam.setRequestId(generageRequestId());
                br = discoverService.queryDiscoverPage(discoverParam);
            }
            super.printJson(response, br.toJsonString());
        } catch (Exception e) {
            LOGGER.error("query", e);
        }
    }

    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String idList = request.getParameter("idList");
        br = discoverService.delete(idList);
        super.printJson(response, br.toJsonString());
    }

}
