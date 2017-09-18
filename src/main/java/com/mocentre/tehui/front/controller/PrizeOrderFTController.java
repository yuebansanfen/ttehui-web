package com.mocentre.tehui.front.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListResult;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.CustomerInfoFTInstance;
import com.mocentre.tehui.frontend.model.PrizeOrderFTInstance;
import com.mocentre.tehui.frontend.param.PrizeOrderQueryParam;
import com.mocentre.tehui.frontend.service.PrizeOrderManageService;

/**
 * 实物奖品订单controller Created by wangxueying on 2017/8/11.
 */
@Controller
@RequestMapping("/front/prizeOrder")
public class PrizeOrderFTController extends BaseController {

    @Autowired
    private PrizeOrderManageService prizeOrderManageService;
    @Autowired
    private RedisCache              redisCache;

    /**
     * 获取列表
     */
    @RequestMapping(value = "getList.htm", method = RequestMethod.POST)
    public void getPrizeOrderList(HttpServletRequest request, HttpServletResponse response) {
        ListResult<PrizeOrderFTInstance> lr = new ListResult<PrizeOrderFTInstance>();
        PrizeOrderQueryParam prizeOrderParam = bindClass(request, PrizeOrderQueryParam.class);
        try {
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            if (userInfo == null) {
                lr.setErrorMessage("1001", "用户不存在");
            }
            if (lr.isSuccess()) {
                Long cumId = userInfo.getId();
                prizeOrderParam.setCustomerId(cumId);
                lr = prizeOrderManageService.prizeOrderList(prizeOrderParam);
            }
        } catch (Exception e) {
            LOGGER.error("getList", e);
            lr.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, lr.toJsonString());
    }

    /**
     * 添加收货地址
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "addAddress.htm", method = RequestMethod.POST)
    public void addPrizeOrderAddress(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        String prizeOrderId = request.getParameter("prizeOrderId");
        String addressId = request.getParameter("addressId");
        if (StringUtils.isBlank(prizeOrderId) || StringUtils.isBlank(addressId)) {
            br.setErrorMessage("1000", "参数错误");
        }
        try {
            if (br.isSuccess()) {
                br = prizeOrderManageService.addAddress(Long.parseLong(prizeOrderId), Long.parseLong(addressId),
                        generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("addAddress", e);
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }

}
