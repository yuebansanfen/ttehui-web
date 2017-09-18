package com.mocentre.gift.gd.controller;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.gift.GiftCategoryManageService;
import com.mocentre.gift.backend.model.GiftCategoryInstance;
import com.mocentre.gift.backend.param.GiftCategoryParam;
import com.mocentre.gift.backend.param.GiftCategoryQueryParam;
import com.mocentre.tehui.core.controller.BaseController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 礼品平台分类controller
 * <p>
 * Created by yukaiji on 2017/04/09.
 */
@Controller
@RequestMapping("/ggoods/category")
public class GiftCategoryController extends BaseController {

    @Autowired
    private GiftCategoryManageService giftCategoryManageService;

    /**
     * 跳转初始化页面
     */
    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index() {
        return getNameSpace() + "index";
    }

    /**
     * 跳转分类添加页面
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.GET)
    public String add() {
        return getNameSpace() + "add";
    }

    /**
     * 跳转分类修改页面
     */
    @RequestMapping(value = "edit.htm", method = RequestMethod.GET)
    public String edit(Long id, Model model) {
        PlainResult<GiftCategoryInstance> pr =  giftCategoryManageService.getGiftCategoryById(id, generageRequestId());
        model.addAttribute("GiftCategory", pr.getData());
        return getNameSpace() + "edit";
    }

    /**
     * 跳转子分类页面
     */
    @RequestMapping(value = "view.htm", method = RequestMethod.GET)
    public String view(Long id, String cType, Model model) {
        model.addAttribute("pid", id);
        model.addAttribute("cType", cType);
        return getNameSpace() + "view";
    }

    /**
     * 跳转子分类添加页面
     */
    @RequestMapping(value = "addChildren.htm", method = RequestMethod.GET)
    public String addChildren(Long pid, String cType, Model model) {
        model.addAttribute("pid", pid);
        model.addAttribute("cType", cType);
        return getNameSpace() + "add";
    }

    /**
     * 跳转子分类修改页面
     */
    @RequestMapping(value = "editChildren.htm", method = RequestMethod.GET)
    public String editChildren(Long id, Model model) {
        model.addAttribute("pid", id);
        return getNameSpace() + "edit";
    }

    /**
     * 分页查询分类
     */
    @RequestMapping(value = "query.htm", method = RequestMethod.POST)
    public void query(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<GiftCategoryInstance> lr = new ListJsonResult<GiftCategoryInstance>();
        try {
            GiftCategoryQueryParam giftCategoryQueryParam = bindClass(request, GiftCategoryQueryParam.class);
            lr = giftCategoryManageService.queryGiftCategoryPage(giftCategoryQueryParam);
        } catch (Exception e) {
            LOGGER.error("query", e);
        }
        super.printJson(response, lr.toJsonString());
    }

    /**
     * 查询具体分类
     */
    @RequestMapping(value = "queryGiftCategory.htm", method = RequestMethod.POST)
    public void queryGiftCategory(HttpServletRequest request, HttpServletResponse response) {
        ListResult<GiftCategoryInstance> lr = new ListResult<GiftCategoryInstance>();
        String cType = request.getParameter("cType");
        String pid = request.getParameter("pid");
        try {
            if (lr.isSuccess()) {
                lr = giftCategoryManageService.queryGiftCategory(cType, pid);
            }
        } catch (Exception e) {
            LOGGER.error("queryGiftCategory", e);
        }
        super.printJson(response, lr.toJsonString());
    }

    /**
     * 添加分类
     */
    @RequestMapping(value = "addGiftCategory.htm", method = RequestMethod.POST)
    public void addGiftCategory(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        GiftCategoryParam giftCategoryParam = super.bindClass(request, GiftCategoryParam.class);
        try {
            if (giftCategoryParam == null) {
                br.setErrorMessage("1001", "参数错误");
            }
            if (br.isSuccess()) {
                br = giftCategoryManageService.addGiftCategory(giftCategoryParam);
            }
        } catch (Exception e) {
            LOGGER.error("addGiftCategory", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 修改分类
     */
    @RequestMapping(value = "editGiftCategory.htm", method = RequestMethod.POST)
    public void editGiftCategory(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        GiftCategoryParam giftCategoryParam = super.bindClass(request, GiftCategoryParam.class);
        try {
            if (giftCategoryParam == null) {
                br.setErrorMessage("1001", "参数错误");
            }
            if (giftCategoryParam.getIsShow() == null) {
                br.setErrorMessage("1001", "参数错误");
            }
            if (giftCategoryParam.getId() == null) {
                br.setErrorMessage("1001", "分类id为空");
            }
            if (br.isSuccess()) {
                br = giftCategoryManageService.updateGiftCategory(giftCategoryParam);
            }
        } catch (Exception e) {
            LOGGER.error("editGiftCategory", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 删除分类
     */
    @RequestMapping(value = "delGiftCategory.htm", method = RequestMethod.POST)
    public void delGiftCategory(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        try {
            if (StringUtils.isBlank(id)) {
                br.setErrorMessage("1001", "分类id为空");
            }
            if (br.isSuccess()) {
                br = giftCategoryManageService.removeById(Long.valueOf(id), generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("delGiftCategory", e);
        }
        super.printJson(response, br.toJsonString());
    }
}
