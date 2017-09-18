package com.mocentre.tehui.bak.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.MapResult;
import com.mocentre.tehui.MallHomeManageService;
import com.mocentre.tehui.backend.model.ImageInstance;
import com.mocentre.tehui.backend.model.MallHomeInstance;
import com.mocentre.tehui.backend.param.ImageParam;
import com.mocentre.tehui.backend.param.ImageQueryParam;
import com.mocentre.tehui.backend.param.MallHomeParam;
import com.mocentre.tehui.backend.param.MallHomeQueryParam;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;

/**
 * 农行掌银 Created by yukaiji on 2017/05/18.
 */
@Controller
@RequestMapping("/bak/mallHome")
public class MallHomeController extends BaseController {

    @Autowired
    private MallHomeManageService mallHomeManageService;

    /**
     * 跳转到首页
     */
    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index() {
        return getNameSpace() + "index";
    }

    /**
     * 跳转到添加页面
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.GET)
    public String add() {
        return getNameSpace() + "add";
    }

    /**
     * 跳转到修改页面
     */
    @RequestMapping(value = "edit.htm", method = RequestMethod.GET)
    public String edit(Long id, Model model, HttpServletRequest request) {
        Long shopId = (Long) request.getSession().getAttribute(SessionKeyConstant.SHOP);
        try {
            MapResult result = mallHomeManageService.getMallHomeById(id, shopId);
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
     * 分页查询
     */
    @RequestMapping(value = "query.htm", method = RequestMethod.POST)
    public void query(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<MallHomeInstance> br = new ListJsonResult<MallHomeInstance>();
        try {
            if (br.isSuccess()) {
                MallHomeQueryParam mallHomeQueryParam = bindDTParamClass(request, MallHomeQueryParam.class);
                br = mallHomeManageService.queryMallHomePage(mallHomeQueryParam);
                super.printJson(response, br.toJsonString());
            }
        } catch (Exception e) {
            LOGGER.error("query", e);
        }
    }

    /**
     * 添加操作
     */
    @RequestMapping(value = "addMallHome.htm", method = RequestMethod.POST)
    public void addMallHome(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            MallHomeParam mallHomeParam = super.bindClass(request, MallHomeParam.class);
            br = mallHomeManageService.addMallHome(mallHomeParam);
        } catch (Exception e) {
            LOGGER.error("addMallHome", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 修改操作
     */
    @RequestMapping(value = "editMallHome.htm", method = RequestMethod.POST)
    public void editMallHome(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            MallHomeParam mallHomeParam = bindClass(request, MallHomeParam.class);
            br = mallHomeManageService.updateMallHome(mallHomeParam);
        } catch (Exception e) {
            LOGGER.error("editMallHome", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 删除
     */
    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        String showLocal = request.getParameter("showLocal");
        try {
            if (!StringUtils.isBlank(id)) {
                br = mallHomeManageService.delById(Long.valueOf(id), showLocal);
            } else {
                br.setErrorMessage("999", "删除失败");
            }

        } catch (Exception e) {
            LOGGER.error("delete", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 跳转到首页
     */
    @RequestMapping(value = "bannerIndex.htm", method = RequestMethod.GET)
    public String bannerIndex() {
        return getNameSpace() + "bannerIndex";
    }

    /**
     * 跳转到添加页面
     */
    @RequestMapping(value = "addBanner.htm", method = RequestMethod.GET)
    public String addBanner() {
        return getNameSpace() + "addBanner";
    }

    /**
     * 跳转到修改页面
     */
    @RequestMapping(value = "editBanner.htm", method = RequestMethod.GET)
    public String editBanner(Long id, Model model, HttpServletRequest request) {
        Long shopId = (Long) request.getSession().getAttribute(SessionKeyConstant.SHOP);
        try {
            MapResult result = mallHomeManageService.getBannerById(id, shopId);
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
            LOGGER.error("editBanner", e);
            model.addAttribute("errorMsg", "系统异常");
            return getErrorIndex();
        }
        return getNameSpace() + "editBanner";
    }

    /**
     * 分页查询
     */
    @RequestMapping(value = "queryBanner.htm", method = RequestMethod.POST)
    public void queryBanner(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<ImageInstance> br = new ListJsonResult<ImageInstance>();
        try {
            if (br.isSuccess()) {
                ImageQueryParam imageQueryParam = bindDTParamClass(request, ImageQueryParam.class);
                br = mallHomeManageService.queryBannerPage(imageQueryParam);
                super.printJson(response, br.toJsonString());
            }
        } catch (Exception e) {
            LOGGER.error("queryBanner", e);
        }
    }

    /**
     * 添加操作
     */
    @RequestMapping(value = "saveBannerInfo.htm", method = RequestMethod.POST)
    public void saveInfo(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            ImageParam imageParam = bindClass(request, ImageParam.class);
            br = mallHomeManageService.addBanner(imageParam);
        } catch (Exception e) {
            LOGGER.error("saveBannerInfo", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 修改操作
     */
    @RequestMapping(value = "editBannerInfo.htm", method = RequestMethod.POST)
    public void editBanner(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            ImageParam imageParam = bindClass(request, ImageParam.class);
            br = mallHomeManageService.updateBanner(imageParam);
        } catch (Exception e) {
            LOGGER.error("editMallHome", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 删除
     */
    @RequestMapping(value = "deleteBanner.htm", method = RequestMethod.POST)
    public void deleteBanner(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        String seat = request.getParameter("seat");
        try {
            if (!StringUtils.isBlank(id)) {
                br = mallHomeManageService.delBannerById(Long.valueOf(id), seat);
            } else {
                br.setErrorMessage("999", "删除失败");
            }

        } catch (Exception e) {
            LOGGER.error("deleteBanner", e);
        }
        super.printJson(response, br.toJsonString());
    }

}
