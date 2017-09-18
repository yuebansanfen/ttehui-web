package com.mocentre.gift.gd.controller;

import com.mocentre.common.*;
import com.mocentre.gift.GiftCategoryManageService;
import com.mocentre.gift.GiftGoodsManageService;
import com.mocentre.gift.backend.model.GiftGoodsInstance;
import com.mocentre.gift.backend.model.GiftGoodsSelectInstance;
import com.mocentre.gift.backend.param.GiftGoodsParam;
import com.mocentre.gift.backend.param.GiftGoodsQueryParam;
import com.mocentre.tehui.core.controller.BaseController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 礼品平台礼品controller
 * <p>
 * Created by yukaiji on 2017/04/09.
 */
@Controller
@RequestMapping("/ggoods/goods")
public class GiftGoodsController extends BaseController {


    @Autowired
    private GiftGoodsManageService      giftGoodsManageService;
    @Autowired
    private GiftCategoryManageService   giftCategoryManageService;

    /**
     * 跳转初始化页面
     */
    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index() {
        return getNameSpace() + "index";
    }

    /**
     * 跳转礼品添加页面
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.GET)
    public String add() {
        return getNameSpace() + "add";
    }

    /**
     * 跳转礼品修改页面
     */
    @RequestMapping(value = "edit.htm", method = RequestMethod.GET)
    public String edit(Long id, Model model) {
        try {
            MapResult result = giftGoodsManageService.editPage(id);
            if (result != null) {
                Map<String, Object> pageInfo = result.getData();
                model.addAttribute("categoryList", pageInfo.get("categoryList"));
                model.addAttribute("category", pageInfo.get("categories"));
                model.addAttribute("giftGoods", pageInfo.get("giftGoods"));
                model.addAttribute("pid", pageInfo.get("pid"));
            }
        } catch (Exception e) {
            LOGGER.error("edit", e);
        }
        return getNameSpace() + "edit";
    }


    /**
     * 分页查询礼品
     */
    @RequestMapping(value = "query.htm", method = RequestMethod.POST)
    public void query(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<GiftGoodsInstance> lr = new ListJsonResult<GiftGoodsInstance>();
        try {
            GiftGoodsQueryParam giftGoodsQueryParam = bindClass(request, GiftGoodsQueryParam.class);
            lr = giftGoodsManageService.queryGiftGoodsPage(giftGoodsQueryParam);
        } catch (Exception e) {
            LOGGER.error("query", e);
        }
        super.printJson(response, lr.toJsonString());
    }

    /**
     * 订单页下拉框
     */
    @RequestMapping(value = "queryForSelect.htm", method = RequestMethod.POST)
    public void queryForSelect(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<GiftGoodsSelectInstance> lr = new ListJsonResult<>();
        try {
            lr = giftGoodsManageService.queryForSelect(generageRequestId());
        } catch (Exception e) {
            LOGGER.error("query", e);
        }
        super.printJson(response, lr.toJsonString());
    }


    /**
     * 根据分类id查询关联商品
     */
    @RequestMapping(value = "queryGoodsByCategoryId.htm", method = RequestMethod.POST)
    public void queryGoodsByCategoryId(HttpServletRequest request, HttpServletResponse response) {
        ListResult<GiftGoodsInstance> lr = new ListResult<GiftGoodsInstance>();
        String categoryId = request.getParameter("categoryId");
        try {
            if (StringUtils.isBlank(categoryId)) {
                lr.setErrorMessage("1001", "分类id为空");
            }
            if (lr.isSuccess()) {
                lr = giftGoodsManageService.queryGoodsByCategoryId(Long.valueOf(categoryId), generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("query", e);
        }
        super.printJson(response, lr.toJsonString());
    }

    /**
     * 根据id查询商品信息
     */
    @RequestMapping(value = "queryGoodsById.htm", method = RequestMethod.POST)
    public void queryGoodsById(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<GiftGoodsInstance> pr = new PlainResult<GiftGoodsInstance>();
        try {
            String id = request.getParameter("id");
            if (StringUtils.isBlank(id)) {
                pr.setErrorMessage("1001", "id为空");
            }
            if (pr.isSuccess()) {
                pr = giftGoodsManageService.getGiftGoodsById(Long.valueOf(id), generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("queryGoodsById", e);
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 添加礼品
     */
    @RequestMapping(value = "addGiftGoods.htm", method = RequestMethod.POST)
    public void addGiftGoods(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<Long> br = new PlainResult<Long>();
        GiftGoodsParam giftGoodsParam = super.bindClass(request, GiftGoodsParam.class);
        try {
            if (giftGoodsParam == null) {
                br.setErrorMessage("1001", "参数错误");
            }
            if (br.isSuccess()) {
                br = giftGoodsManageService.addGiftGoods(giftGoodsParam);
            }
        } catch (Exception e) {
            LOGGER.error("addGiftGoods", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 修改礼品
     */
    @RequestMapping(value = "editGiftGoods.htm", method = RequestMethod.POST)
    public void editGiftGoods(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        GiftGoodsParam giftGoodsParam = super.bindClass(request, GiftGoodsParam.class);
        try {
            if (giftGoodsParam == null) {
                br.setErrorMessage("1001", "参数错误");
            }
            if (giftGoodsParam.getId() == null) {
                br.setErrorMessage("1001", "礼品id为空");
            }
            if (br.isSuccess()) {
                br = giftGoodsManageService.updateGiftGoods(giftGoodsParam);
            }
        } catch (Exception e) {
            LOGGER.error("editGiftGoods", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 删除礼品
     */
    @RequestMapping(value = "delGiftGoods.htm", method = RequestMethod.POST)
    public void delGiftGoods(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        try {
            if (StringUtils.isBlank(id)) {
                br.setErrorMessage("1001", "礼品id为空");
            }
            if (br.isSuccess()) {
                br = giftGoodsManageService.removeById(Long.valueOf(id), generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("delGiftGoods", e);
        }
        super.printJson(response, br.toJsonString());
    }
}
