package com.mocentre.tehui.goods.controller;

import java.util.ArrayList;
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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.AreasManageService;
import com.mocentre.tehui.AttributeManageService;
import com.mocentre.tehui.CategoryManageService;
import com.mocentre.tehui.GoodsManageService;
import com.mocentre.tehui.backend.model.AttributeInstance;
import com.mocentre.tehui.backend.model.CategoryInstance;
import com.mocentre.tehui.backend.model.GoodsAdvancedInstance;
import com.mocentre.tehui.backend.model.GoodsBaseInstance;
import com.mocentre.tehui.backend.model.GoodsInstance;
import com.mocentre.tehui.backend.model.GoodsParamInstance;
import com.mocentre.tehui.backend.model.GoodsShareInstance;
import com.mocentre.tehui.backend.model.GoodsStorageAttributeInstance;
import com.mocentre.tehui.backend.model.GoodsStorageInstance;
import com.mocentre.tehui.backend.param.GoodsBaseInfoParam;
import com.mocentre.tehui.backend.param.GoodsQueryParam;
import com.mocentre.tehui.backend.param.GoodsStorageParam;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;

/**
 * 商品controller Created by 王雪莹 on 2016/11/11.
 */
@Controller
@RequestMapping("/goods/goods")
public class GoodsController extends BaseController {

    @Autowired
    private GoodsManageService     goodsManageService;
    @Autowired
    private AreasManageService     areasManageServeice;
    @Autowired
    private CategoryManageService  categoryManageService;
    @Autowired
    private AttributeManageService attributeService;

    @RequestMapping(value = "ajax/areas.htm", method = RequestMethod.POST)
    public void areas(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<String> result = new PlainResult<String>();
        try {
            String areas = areasManageServeice.getAllProvinceTwoLinkage();
            result.setData(areas);
        } catch (Exception e) {
            LOGGER.error("areas", e);
        }
        super.printJson(response, result.toJsonString());
    }

    @RequestMapping(value = "ajax/attribute.htm", method = RequestMethod.POST)
    public void attribute(HttpServletRequest request, HttpServletResponse response) {
        JSONArray attrArr = new JSONArray();
        try {
            ListResult<AttributeInstance> lr = attributeService.queryList(generageRequestId());
            if (lr.isSuccess()) {
                List<AttributeInstance> list = lr.getData();
                for (AttributeInstance attrIns : list) {
                    JSONObject jobj = new JSONObject();
                    jobj.put("id", attrIns.getId());
                    jobj.put("code", attrIns.getCode());
                    jobj.put("name", attrIns.getName());
                    attrArr.add(jobj);
                }
            }
        } catch (Exception e) {
            LOGGER.error("attribute", e);
        }
        super.printJson(response, attrArr.toJSONString());
    }

    @RequestMapping(value = "ajax/category.htm", method = RequestMethod.POST)
    public void category(HttpServletRequest request, HttpServletResponse response) {
        JSONArray categoryArr = new JSONArray();
        try {
            ListResult<CategoryInstance> lr = categoryManageService.getAllCategoryList(generageRequestId());
            if (lr.isSuccess()) {
                List<CategoryInstance> list = lr.getData();
                for (CategoryInstance categoryInstance : list) {
                    JSONObject jobj = new JSONObject();
                    jobj.put("id", categoryInstance.getId());
                    jobj.put("name", categoryInstance.getName());
                    categoryArr.add(jobj);
                }
            }
        } catch (Exception e) {
            LOGGER.error("category", e);
        }
        super.printJson(response, categoryArr.toJSONString());
    }

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
    public String add(Model model, HttpServletRequest request, HttpServletResponse response) {
        return getNameSpace() + "add";
    }

    /**
     * 跳转基本信息修改页面
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "editBase.htm", method = RequestMethod.GET)
    public String editbase(Model model, HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        try {
            if (StringUtils.isEmpty(id)) {
                model.addAttribute("errorMsg", "参数错误");
                return getErrorIndex();
            }
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            PlainResult<GoodsBaseInstance> pr = goodsManageService.getGoodsBaseInfoById(Long.parseLong(id),
                    (Long) shopId, generageRequestId());
            if (!pr.isSuccess()) {
                model.addAttribute("errorMsg", pr.getMessage());
                return getErrorIndex();
            }
            GoodsBaseInstance goodsBase = pr.getData();
            model.addAttribute("carryModeList", goodsBase.getCarryModeList());
            model.addAttribute("categoryList", goodsBase.getCategoryList());
            model.addAttribute("goods", goodsBase.getGoods());
            model.addAttribute("goodsLaunchList", goodsBase.getGoodsLaunchList());
            model.addAttribute("id", id);
        } catch (Exception e) {
            LOGGER.error("editbase", e);
        }
        return getNameSpace() + "editBase";
    }

    /**
     * 跳转商品详情页
     * 
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "editDetail.htm", method = RequestMethod.GET)
    public String editDetail(Model model, HttpServletRequest request, HttpServletResponse response) {
        try {
            String id = request.getParameter("id");
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            PlainResult<GoodsInstance> pr = goodsManageService.getGoodsById(Long.parseLong(id), (Long) shopId,
                    generageRequestId());
            if (pr.isSuccess()) {
                GoodsInstance goodsIns = pr.getData();
                model.addAttribute("goodsIns", goodsIns);
            } else {
                model.addAttribute("errorMgs", pr.getMessage());
                return getErrorIndex();
            }
        } catch (Exception e) {
            LOGGER.error("editDetail", e);
            model.addAttribute("errorMgs", "系统异常");
            return getErrorIndex();
        }

        return getNameSpace() + "editDetail";
    }

    /**
     * 跳转库存信息修改页面
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "editStorage.htm", method = RequestMethod.GET)
    public String editStorage(Model model, HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        try {
            PlainResult<GoodsStorageAttributeInstance> pr = goodsManageService.getStorageInfoById(Long.parseLong(id),
                    generageRequestId());
            GoodsStorageAttributeInstance storageCategory = pr.getData();
            List<AttributeInstance> attrList = storageCategory.getAttributeList();
            List<GoodsStorageInstance> storageList = storageCategory.getGoodsStorageList();
            GoodsInstance goods = storageCategory.getGoods();
            JSONArray attrJsonArr = new JSONArray();
            if (attrList != null) {
                for (AttributeInstance attr : attrList) {
                    JSONObject attrObj = new JSONObject();
                    attrObj.put("id", attr.getId());
                    attrObj.put("code", attr.getCode());
                    attrObj.put("name", attr.getName());
                    attrJsonArr.add(attrObj);
                }
            }
            model.addAttribute("attrList", JSON.toJSON(attrJsonArr));
            model.addAttribute("storageList", storageList);
            model.addAttribute("goods", goods);
        } catch (Exception e) {
            LOGGER.error("editStorage", e);
        }
        return getNameSpace() + "editStorage";
    }

    /**
     * 跳转商品分享修改页面
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "editShare.htm", method = RequestMethod.GET)
    public String editShare(HttpServletRequest request, HttpServletResponse response, Model model) {
        PlainResult<GoodsShareInstance> pr = new PlainResult<GoodsShareInstance>();
        try {
            String goodsId = request.getParameter("id");
            if (StringUtils.isBlank(goodsId)) {
                model.addAttribute("errorMsg", "参数错误");
                return getErrorIndex();
            }
            pr = goodsManageService.getGoodsShareInfo(Long.parseLong(goodsId), generageRequestId());
            if (pr.isSuccess()) {
                model.addAttribute("goodsShare", pr.getData());
            } else {
                model.addAttribute("errorMsg", pr.getMessage());
                return getErrorIndex();
            }
            model.addAttribute("goodsId", goodsId);
        } catch (Exception e) {
            LOGGER.error("editShare", e);
        }
        return getNameSpace() + "editShare";
    }

    /**
     * 跳转商品参数修改页面
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "editParam.htm", method = RequestMethod.GET)
    public String editParam(HttpServletRequest request, HttpServletResponse response, Model model) {
        ListResult<GoodsParamInstance> lr = new ListResult<GoodsParamInstance>();
        String goodsId = request.getParameter("id");
        try {
            if (StringUtils.isBlank(goodsId)) {
                model.addAttribute("errorMsg", "参数错误");
                return getErrorIndex();
            }
            Long shopId = (Long) request.getSession().getAttribute(SessionKeyConstant.SHOP);
            lr = goodsManageService.getGoodsParamInfo(shopId, Long.parseLong(goodsId), generageRequestId());
            if (lr.isSuccess()) {
                model.addAttribute("goodsParamList", lr.getData());
            } else {
                model.addAttribute("errorMsg", lr.getMessage());
                return getErrorIndex();
            }
            model.addAttribute("goodsId", goodsId);
        } catch (Exception e) {
            LOGGER.error("editParam", e);
        }
        return getNameSpace() + "editParam";
    }

    /**
     * 更新商品审核状态
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "updateGoodsCheck.htm", method = RequestMethod.POST)
    public void updateGoodsCheck(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        try {
            Long shopId = (Long) request.getSession().getAttribute(SessionKeyConstant.SHOP);
            String id = request.getParameter("id");
            String status = request.getParameter("status");
            if (StringUtils.isBlank(id) || StringUtils.isBlank(status)) {
                result.setErrorMessage("100", "参数错误");
            }
            if (result.isSuccess()) {
                result = goodsManageService.updateGoodsStatus(Long.parseLong(id), shopId, status, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("updateGoodsCheck", e);
            result.setErrorMessage("99", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 更新商品上下架状态
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "updateGoodsShow.htm", method = RequestMethod.POST)
    public void updateGoodsShow(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        try {
            Long shopId = (Long) request.getSession().getAttribute(SessionKeyConstant.SHOP);
            String id = request.getParameter("id");
            String status = request.getParameter("status");
            if (StringUtils.isBlank(id) || StringUtils.isBlank(status)) {
                result.setErrorMessage("100", "参数错误");
            }
            if (result.isSuccess()) {
                result = goodsManageService.updateGoodsStatus(Long.parseLong(id), shopId, status, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("updateGoodsShow", e);
            result.setErrorMessage("99", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 分页查询
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "queryPage.htm", method = RequestMethod.POST)
    public void queryPage(HttpServletRequest request, HttpServletResponse response) {
        Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
        ListJsonResult<GoodsInstance> result = new ListJsonResult<GoodsInstance>();
        try {
            if (shopId == null) {
                result.setData(new ArrayList<GoodsInstance>());
                result.setErrorMessage("1001", "店铺id为空");
            }
            if (result.isSuccess()) {
                GoodsQueryParam queryParam = bindClass(request, GoodsQueryParam.class);
                queryParam.setShopId((Long) shopId);
                result = goodsManageService.queryPage(queryParam);
            }
        } catch (Exception e) {
            LOGGER.error("queryPage", e);
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 删除
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "delById.htm", method = RequestMethod.POST)
    public void delById(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            String id = request.getParameter("id");
            if (StringUtils.isNotEmpty(id)) {
                br = goodsManageService.deleteById(Long.parseLong(id), (Long) shopId, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("delete", e);
            br.setErrorMessage("999", "删除失败");
        }
        printJson(response, br.toJsonString());
    }

    /**
     * 查询商品基本信息
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "ajax/getGoodsById.htm", method = RequestMethod.POST)
    public void getGoodsById(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        PlainResult<GoodsInstance> pr = new PlainResult<GoodsInstance>();
        try {
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            if (StringUtils.isEmpty(id)) {
                pr.setErrorMessage("100", "参数错误");
            }
            pr = goodsManageService.getGoodsById(Long.parseLong(id), (Long) shopId, generageRequestId());
        } catch (Exception e) {
            LOGGER.error("getInfoById", e);
            pr.setErrorMessage("999", "系统异常");
        }
        printJson(response, pr.toJsonString());
    }

    @RequestMapping(value = "ajax/getByCategory.htm", method = RequestMethod.POST)
    public void getByCategory(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<JSONArray> pr = new PlainResult<JSONArray>();
        try {
            String categoryId = request.getParameter("cid");
            if (StringUtils.isBlank(categoryId)) {
                pr.setErrorMessage("1000", "参数不能为空");
            }
            if (pr.isSuccess()) {
                ListResult<GoodsInstance> lr = goodsManageService.queryOnShelfGoodsByCategory(
                        Long.parseLong(categoryId), generageRequestId());
                JSONArray idNameArr = new JSONArray();
                if (lr.isSuccess()) {
                    List<GoodsInstance> lists = lr.getData();
                    if (lists != null) {
                        for (int i = 0; i < lists.size(); i++) {
                            GoodsInstance goodsObj = lists.get(i);
                            JSONObject idNameObj = new JSONObject();
                            idNameObj.put("id", goodsObj.getId());
                            idNameObj.put("name", goodsObj.getTitle());
                            idNameObj.put("holdStandard", goodsObj.getHoldStandard());
                            idNameArr.add(idNameObj);
                        }
                    }
                }
                pr.setData(idNameArr);
            }
        } catch (Exception e) {
            LOGGER.error("getByCategory", e);
            pr.setErrorMessage("999", "系统异常");
        }
        printJson(response, pr.toJsonString());
    }

    /**
     * 添加或修改商品基础信息
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "addOrUpdateBaseInfo.htm", method = RequestMethod.POST)
    public void addOrUpdateBaseInfo(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<Long> result = new PlainResult<>();
        try {
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            if (shopId == null) {
                result.setErrorMessage("1000", "不存在店铺，无法添加商品");
            }
            if (result.isSuccess()) {
                String paramMap = request.getParameter("paramMap");
                GoodsBaseInfoParam gf = JSON.parseObject(paramMap, GoodsBaseInfoParam.class);
                gf.setShopId((Long) shopId);
                gf.setRequestId(generageRequestId());
                result = goodsManageService.addOrUpdateGoodsBaseInfo(gf);
            }
        } catch (Exception e) {
            LOGGER.error("addOrUpdateBaseInfo", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 添加或修改商品库存信息
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "addOrUpdateSkuInfo.htm", method = RequestMethod.POST)
    public void addOrUpdateSkuInfo(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        try {
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            if (shopId == null) {
                result.setErrorMessage("1000", "不存在店铺，无法添加");
            }
            if (result.isSuccess()) {
                String paramMap = request.getParameter("paramMap");
                JSONObject jObject = JSON.parseObject(paramMap);
                String goodsId = jObject.getString("goodsId");
                String standard = jObject.getString("standard");
                String goodsStorageList = jObject.getString("goodsStorageList");
                List<GoodsStorageParam> gsList = JSON.parseArray(goodsStorageList, GoodsStorageParam.class);
                if (StringUtils.isNotEmpty(goodsId)) {
                    result = goodsManageService.addOrUpdateStorage(Long.parseLong(goodsId), standard, (Long) shopId,
                            gsList, generageRequestId());
                } else {
                    result.setErrorMessage("1001", "添加失败");
                }
            }
        } catch (Exception e) {
            LOGGER.error("addOrUpdateSkuInfo", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 添加或修改商品详情信息
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "addOrUpdateDetailInfo.htm", method = RequestMethod.POST)
    public void addOrUpdateDetailInfo(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        try {
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            if (shopId == null) {
                result.setErrorMessage("1000", "不存在店铺，无法添加");
            }
            if (result.isSuccess()) {
                String paramMap = request.getParameter("paramMap");
                JSONObject jObject = JSON.parseObject(paramMap);
                Long goodsId = jObject.getLong("goodsId");
                String imgBanner = jObject.getString("imgBanner");
                String details = jObject.getString("details");
                if (goodsId != null) {
                    result = goodsManageService.addGoodsDetailInfo(goodsId, (Long) shopId, imgBanner, details,
                            generageRequestId());
                } else {
                    result.setErrorMessage("1001", "添加失败");
                }
            }
        } catch (Exception e) {
            LOGGER.error("addOrUpdateDetailInfo", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 添加或修改商品参数信息
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "addOrUpdateParamInfo.htm", method = RequestMethod.POST)
    public void addOrUpdateParamInfo(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        try {
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            if (shopId == null) {
                result.setErrorMessage("1000", "不存在店铺，无法添加");
            }
            if (result.isSuccess()) {
                String paramMap = request.getParameter("paramMap");
                JSONObject jObject = JSON.parseObject(paramMap);
                Object goodsId = jObject.remove("goodsId");
                String paramList = jObject.getString("paramList");
                if (goodsId != null) {
                    result = goodsManageService.addGoodsParamInfo((Long) shopId, Long.parseLong(goodsId.toString()),
                            paramList, generageRequestId());
                } else {
                    result.setErrorMessage("1001", "添加失败");
                }
            }
        } catch (Exception e) {
            LOGGER.error("addOrUpdateParamInfo", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 添加或修改商品分享信息
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "addOrUpdateShareInfo.htm", method = RequestMethod.POST)
    public void addOrUpdateShareInfo(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        String paramMap = request.getParameter("paramMap");
        JSONObject jObject = JSON.parseObject(paramMap);
        Object goodsId = jObject.remove("goodsId");
        try {
            if (goodsId != null) {
                result = goodsManageService.addGoodsShareInfo(Long.parseLong(goodsId.toString()), jObject,
                        generageRequestId());
            } else {
                result.setErrorMessage("1001", "添加失败");
            }
        } catch (Exception e) {
            LOGGER.error("addOrUpdateShareInfo", e);
            result.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, result.toJsonString());
    }

    @RequestMapping(value = "ajax/getGoodsAndStorageById.htm", method = RequestMethod.POST)
    public void getGoodsAndStorageById(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<GoodsAdvancedInstance> pr = new PlainResult<GoodsAdvancedInstance>();
        String idStr = request.getParameter("id");
        try {
            if (StringUtils.isNotBlank(idStr)) {
                pr = goodsManageService.getGoodsAndStorageById(Long.parseLong(idStr), generageRequestId());
                if (!pr.isSuccess()) {
                    pr.setErrorMessage("1002", pr.getMessage());
                }
            } else {
                pr.setErrorMessage("1000", "参数错误");
            }
        } catch (Exception e) {
            LOGGER.error("getGoodsAndStorageById", e);
            pr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, pr.toJsonString());
    }

}
