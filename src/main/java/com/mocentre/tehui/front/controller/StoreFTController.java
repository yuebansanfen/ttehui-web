package com.mocentre.tehui.front.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.CustomerInfoFTInstance;
import com.mocentre.tehui.frontend.model.StoreFTInstance;
import com.mocentre.tehui.frontend.param.StoreParam;
import com.mocentre.tehui.frontend.service.StoreManageService;

/**
 * 商品收藏调用接口
 *
 * @author Created by yukaiji on 2016年12月23日
 */
@Controller
@RequestMapping("/front/store")
public class StoreFTController extends BaseController {

    @Autowired
    private StoreManageService storeManageService;
    @Autowired
    private RedisCache         redisCache;

    /**
     * 跳转到收藏商品详情页面
     */
    @RequestMapping(value = "getStoreList.htm", method = RequestMethod.POST)
    public void getStoreList(HttpServletResponse response) {
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        ListResult<StoreFTInstance> lr = new ListResult<StoreFTInstance>();
        try {
            if (userInfo == null) {
                lr.setErrorMessage("1001", "用户id为空");
            }
            if (lr.isSuccess()) {
                lr = storeManageService.getStoreList(userInfo.getId(), generageRequestId());
            }
        } catch (Exception e) {
            lr.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, lr.toJsonString());
    }

    /**
     * 根据id和用户ID获取收藏信息
     */
    @RequestMapping(value = "queryStoreById.htm", method = RequestMethod.POST)
    public void queryStoreById(HttpServletRequest request, HttpServletResponse response) {
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        PlainResult<StoreFTInstance> pr = new PlainResult<StoreFTInstance>();
        String id = request.getParameter("id");
        try {
            if (userInfo == null) {
                pr.setErrorMessage("1001", "用户id为空");
            }
            if (StringUtils.isBlank(id)) {
                pr.setErrorMessage("1001", "收藏id为空");
            }
            if (pr.isSuccess()) {
                pr = storeManageService.getStore(Long.valueOf(id), userInfo.getId(), generageRequestId());
            }
        } catch (Exception e) {
            pr.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 添加，删除收藏
     */
    @RequestMapping(value = "saveStore.htm", method = RequestMethod.POST)
    public void saveStore(HttpServletRequest request, HttpServletResponse response) {
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        BaseResult br = new BaseResult();
        try {
            StoreParam storeParam = super.bindClass(request, StoreParam.class);
            boolean storePass = storeParam.getGoodsId() == null || storeParam.getShopId() == null
                    || storeParam.getStatus() == null;
            if (storePass) {
                br.setErrorMessage("1000", "参数错误");
            }
            if (userInfo == null) {
                br.setErrorMessage("1001", "用户不存在");
            }
            if (br.isSuccess()) {
                storeParam.setCustomerId(userInfo.getId());
                storeParam.setRequestId(generageRequestId());
                br = storeManageService.saveStore(storeParam);
            }
        } catch (Exception e) {
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

}
