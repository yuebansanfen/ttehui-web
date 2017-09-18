package com.mocentre.tehui.act.controller;

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
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.act.service.ActivityService;
import com.mocentre.tehui.backend.model.ActivityAllMsgInstance;
import com.mocentre.tehui.backend.model.ActivityGoodsInstance;
import com.mocentre.tehui.backend.model.ActivityInstance;
import com.mocentre.tehui.backend.model.GrouponInstance;
import com.mocentre.tehui.backend.param.ActivityGoodsParam;
import com.mocentre.tehui.backend.param.ActivityGoodsQueryParam;
import com.mocentre.tehui.backend.param.ActivityParam;
import com.mocentre.tehui.backend.param.ActivityQueryParam;
import com.mocentre.tehui.backend.param.GoodsStorageParam;
import com.mocentre.tehui.backend.param.GrouponQueryParam;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;

/**
 * 活动controller Created by yukaiji on 2017/01/16.
 */
@Controller
@RequestMapping("/act/activity")
public class ActivityController extends BaseController {

    @Autowired
    private ActivityService activityService;

    /**
     * 跳转到首页
     */
    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index() {
        return getNameSpace() + "index";
    }

    /**
     * 跳转到活动与商品关联详细页
     */
    @RequestMapping(value = "view.htm", method = RequestMethod.GET)
    public String view(Long id, Model model, HttpServletRequest request) {
        try {
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            PlainResult<ActivityInstance> pr = activityService.activityEdit(id, Long.parseLong(shopId.toString()));
            if (pr.isSuccess()) {
                model.addAttribute("activity", pr.getData());
            } else {
                model.addAttribute("errorMsg", pr.getMessage());
                return getErrorIndex();
            }
        } catch (Exception e) {
            LOGGER.error("view", e);
            model.addAttribute("errorMsg", "系统异常");
            return getErrorIndex();
        }
        return getNameSpace() + "view";
    }

    /**
     * 跳转到商品拼团列表页
     */
    @RequestMapping(value = "grouponIndex.htm", method = RequestMethod.GET)
    public String grouponIndex(Model model, HttpServletRequest request) {
        try {
            String id = request.getParameter("id");
            String pid = request.getParameter("pid");
            String type = request.getParameter("type");
            model.addAttribute("id", Long.valueOf(id));
            model.addAttribute("pid", Long.valueOf(pid));
            model.addAttribute("type", type);
        } catch (Exception e) {
            LOGGER.error("grouponIndex", e);
            model.addAttribute("errorMsg", "系统异常");
            return getErrorIndex();
        }
        return getNameSpace() + "grouponIndex";
    }

    /**
     * 跳转到活动添加页面
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.GET)
    public String add() {
        return getNameSpace() + "add";
    }

    /**
     * 分页查询活动
     */
    @RequestMapping(value = "query.htm", method = RequestMethod.POST)
    public void query(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<ActivityInstance> br = new ListJsonResult<ActivityInstance>();
        Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
        try {
            if (shopId == null) {
                br.setErrorMessage("1001", "店铺id为空");
            }
            if (br.isSuccess()) {
                ActivityQueryParam activityQueryParam = bindDTParamClass(request, ActivityQueryParam.class);
                activityQueryParam.setShopId(Long.valueOf(shopId.toString()));
                br = activityService.queryActivityPage(activityQueryParam);
                super.printJson(response, br.toJsonString());
            }
        } catch (Exception e) {
            LOGGER.error("query", e);
        }
    }

    /**
     * 分页查询活动所属商品信息
     */
    @RequestMapping(value = "queryActivityGoods.htm", method = RequestMethod.POST)
    public void queryActivityGoods(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<ActivityGoodsInstance> br = new ListJsonResult<>();
        try {
            String id = request.getParameter("id");
            if (id != null && !(id.equals(""))) {
                ActivityGoodsQueryParam actGoodsQueryParam = bindClass(request, ActivityGoodsQueryParam.class);
                actGoodsQueryParam.setActivityId(Long.valueOf(id));
                br = activityService.queryActivityGoodsPage(actGoodsQueryParam);
            } else {
                br.setErrorMessage("999", "请求参数错误");
            }
            super.printJson(response, br.toJsonString());
        } catch (Exception e) {
            LOGGER.error("queryActivityGoods", e);
        }
    }

    /**
     * 分页查询活动
     */
    @RequestMapping(value = "queryGroupons.htm", method = RequestMethod.POST)
    public void queryGroupons(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<GrouponInstance> br = new ListJsonResult<GrouponInstance>();
        Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
        try {
            String id = request.getParameter("id");
            if (StringUtils.isBlank(id)) {
                br.setErrorMessage("1001", "活动商品id为空");
            }
            if (shopId == null) {
                br.setErrorMessage("1001", "店铺id为空");
            }
            if (br.isSuccess()) {
                GrouponQueryParam grouponQueryParam = bindClass(request, GrouponQueryParam.class);
                grouponQueryParam.setActGoodsId(Long.valueOf(id));
                grouponQueryParam.setShopId(Long.valueOf(shopId.toString()));
                br = activityService.queryGrouponPage(grouponQueryParam);
            }
        } catch (Exception e) {
            LOGGER.error("query", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 跳转到修改页面
     */
    @RequestMapping(value = "edit.htm", method = RequestMethod.GET)
    public String edit(Long id, Model model, HttpServletRequest request) {
        try {
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            PlainResult<ActivityInstance> pr = activityService.activityEdit(id, Long.parseLong(shopId.toString()));
            if (pr.isSuccess()) {
                ActivityInstance result = pr.getData();
                model.addAttribute("activity", result);
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
     * 跳转到商品添加页面
     */
    @RequestMapping(value = "addGoods.htm", method = RequestMethod.GET)
    public String addGoods(Model model, HttpServletRequest request) {
        String pid = request.getParameter("pid");
        try {
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            PlainResult<ActivityInstance> pr = activityService.activityEdit(Long.parseLong(pid),
                    Long.parseLong(shopId.toString()));
            if (pr.isSuccess()) {
                ActivityInstance result = pr.getData();
                model.addAttribute("activity", result);
                model.addAttribute("imageList", JSON.toJSONString(result.getImageList()));
            } else {
                model.addAttribute("errorMsg", "访问失败");
                return getErrorIndex();
            }
        } catch (Exception e) {
            LOGGER.error("addGoods", e);
            model.addAttribute("errorMsg", "系统异常");
            return getErrorIndex();
        }
        return getNameSpace() + "addgoods";
    }

    /**
     * 跳转到关联商品修改页面
     */
    @RequestMapping(value = "editGoods.htm", method = RequestMethod.GET)
    public String editGoods(Model model, HttpServletRequest request) {
        try {
            String id = request.getParameter("id");
            String pid = request.getParameter("pid");
            PlainResult<ActivityAllMsgInstance> pr = activityService.activityGoodsEdit(Long.parseLong(pid),
                    Long.parseLong(id));
            if (pr.isSuccess()) {
                ActivityAllMsgInstance actAllMsg = pr.getData();
                model.addAttribute("activityGoods", actAllMsg.getActivityGoodsInstance());
                model.addAttribute("activity", actAllMsg.getActivityInstance());
            } else {
                model.addAttribute("errorMsg", pr.getMessage());
                return getErrorIndex();
            }
        } catch (Exception e) {
            LOGGER.error("edit", e);
            model.addAttribute("errorMsg", "系统异常");
            return getErrorIndex();
        }
        return getNameSpace() + "editgoods";
    }

    /**
     * 添加活动操作
     */
    @RequestMapping(value = "addActivity.htm", method = RequestMethod.POST)
    public void addActivity(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            if (shopId != null) {
                ActivityParam actParam = super.bindClass(request, ActivityParam.class);
                actParam.setShopId((Long) shopId);
                br = activityService.addActivity(actParam);
            } else {
                br.setErrorMessage("100", "添加失败");
            }
        } catch (Exception e) {
            br.setErrorMessage("999", "系统异常");
            LOGGER.error("addActivity", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 修改活动操作
     */
    @RequestMapping(value = "editActivity.htm", method = RequestMethod.POST)
    public void editActivity(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            if (shopId != null) {
                ActivityParam actParam = super.bindClass(request, ActivityParam.class);
                actParam.setShopId((Long) (shopId));
                br = activityService.editActivity(actParam);
            } else {
                br.setErrorMessage("100", "修改失败");
            }
        } catch (Exception e) {
            br.setErrorMessage("999", "系统异常");
            LOGGER.error("editActivity", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 删除一个活动
     */
    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        try {
            br = activityService.delete(Long.parseLong(id), generageRequestId());
        } catch (Exception e) {
            br.setErrorMessage("999", "系统异常");
            LOGGER.error("delete", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 是否显示更改操作
     */
    @RequestMapping(value = "updateshow.htm", method = RequestMethod.POST)
    public void show(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        String show = request.getParameter("show");
        try {
            br = activityService.show(id, show);
        } catch (Exception e) {
            br.setErrorMessage("999", "系统异常");
            LOGGER.error("show", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 新增活动商品
     */
    @RequestMapping(value = "addActivityGoods.htm", method = RequestMethod.POST)
    public void addActivityGoods(Model model, HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            String storageList = request.getParameter("storageList");
            ActivityGoodsParam actGoodsParam = super.bindClass(request, ActivityGoodsParam.class);
            boolean paramValid = actGoodsParam.getActivityId() == null
                    || StringUtils.isBlank(actGoodsParam.getGoodsImg());
            if (StringUtils.isBlank(storageList) || paramValid) {
                br.setErrorMessage("1000", "参数错误");
            }
            if (br.isSuccess()) {
                List<GoodsStorageParam> goodsSP = JSON.parseArray(storageList, GoodsStorageParam.class);
                actGoodsParam.setGoodsSP(goodsSP);
                br = activityService.addActivityGoods(actGoodsParam);
            }
        } catch (Exception e) {
            LOGGER.error("addActivityGoods", e);
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 修改活动商品
     */
    @RequestMapping(value = "updateActivityGoods.htm", method = RequestMethod.POST)
    public void updateActivityGoods(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            String storageList = request.getParameter("storageList");
            ActivityGoodsParam actGoodsParam = super.bindClass(request, ActivityGoodsParam.class);
            boolean paramValid = actGoodsParam.getActivityId() == null
                    || StringUtils.isBlank(actGoodsParam.getGoodsImg());
            if (StringUtils.isBlank(storageList) || paramValid) {
                br.setErrorMessage("1000", "参数错误");
            }
            if (br.isSuccess()) {
                List<GoodsStorageParam> goodsSP = JSON.parseArray(storageList, GoodsStorageParam.class);
                actGoodsParam.setGoodsSP(goodsSP);
                br = activityService.editActivityGoods(actGoodsParam);
            }
        } catch (Exception e) {
            br.setErrorMessage("999", "系统异常");
            LOGGER.error("updateActivityGoods", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 删除一个活动商品
     */
    @RequestMapping(value = "deleteGoods.htm", method = RequestMethod.POST)
    public void deleteGoods(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        try {
            br = activityService.deleteGoods(Long.parseLong(id), generageRequestId());
        } catch (Exception e) {
            br.setErrorMessage("999", "系统异常");
            LOGGER.error("deleteGoods", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 根据条件获取活动列表
     */
    @RequestMapping(value = "ajax/queryActivityList.htm", method = RequestMethod.POST)
    public void queryActivityList(HttpServletRequest request, HttpServletResponse response) {
        ListResult<ActivityInstance> lr = new ListResult<ActivityInstance>();
        try {
            Long shopId = (Long) request.getSession().getAttribute(SessionKeyConstant.SHOP);
            String type = request.getParameter("type");
            if (shopId != null) {
                lr = activityService.queryActivityListByParam(shopId, type, generageRequestId());
            } else {
                lr.setErrorMessage("999", "店铺id为空");
            }
        } catch (Exception e) {
            lr.setErrorMessage("999", "系统异常");
            LOGGER.error("queryActivityList", e);
        }
        super.printJson(response, lr.toJsonString());
    }

    /**
     * 根据条件获取活动商品
     */
    @RequestMapping(value = "ajax/queryActivityGoodsList.htm", method = RequestMethod.POST)
    public void queryActivityGoodsList(HttpServletRequest request, HttpServletResponse response) {
        ListResult<ActivityGoodsInstance> lr = new ListResult<ActivityGoodsInstance>();
        try {
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            String activityId = request.getParameter("activityId");
            if (StringUtils.isBlank(activityId)) {
                lr.setErrorMessage("999", "参数错误");
            }
            if (shopId == null) {
                lr.setErrorMessage("999", "店铺id为空");
            }
            if (lr.isSuccess()) {
                lr = activityService.queryActivityListByActivityId(Long.valueOf(activityId), generageRequestId());
            }
        } catch (Exception e) {
            lr.setErrorMessage("999", "系统异常");
            LOGGER.error("queryActivityGoodsList", e);
        }
        super.printJson(response, lr.toJsonString());
    }

    /**
     * 根据条件获取活动商品
     */
    @RequestMapping(value = "queryActGoodsInfo.htm", method = RequestMethod.POST)
    public void queryActGoodsInfo(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<ActivityGoodsInstance> pr = new PlainResult<ActivityGoodsInstance>();
        try {
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            String id = request.getParameter("id");
            if (StringUtils.isBlank(id)) {
                pr.setErrorMessage("999", "参数错误");
            }
            if (shopId == null) {
                pr.setErrorMessage("999", "店铺id为空");
            }
            if (pr.isSuccess()) {
                pr = activityService.queryActivityGoodsInfo(Long.valueOf(id), generageRequestId());
            }
        } catch (Exception e) {
            pr.setErrorMessage("999", "系统异常");
            LOGGER.error("queryActGoodsInfo", e);
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 批量修改活动商品起止时间
     */
    @RequestMapping(value = "updateActGoodsTime.htm", method = RequestMethod.POST)
    public void updateActGoodsTime(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        try {
            String activityId = request.getParameter("activityId");
            String beginTime = request.getParameter("beginTime");
            String endTime = request.getParameter("endTime");
            if (StringUtils.isBlank(activityId)) {
                result.setErrorMessage("999", "活动id为空");
            }
            if (StringUtils.isBlank(beginTime)) {
                result.setErrorMessage("999", "开始时间为空");
            }
            if (StringUtils.isBlank(endTime)) {
                result.setErrorMessage("999", "结束时间为空");
            }
            if (result.isSuccess()) {
                result = activityService.updateAllActGoodsTime(Long.valueOf(activityId), beginTime, endTime);
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统异常");
            LOGGER.error("updateActGoodsTime", e);
        }
        super.printJson(response, result.toJsonString());
    }

}
