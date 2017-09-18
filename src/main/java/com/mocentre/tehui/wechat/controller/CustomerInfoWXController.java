package com.mocentre.tehui.wechat.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.CustomerAddressFTInstance;
import com.mocentre.tehui.frontend.model.CustomerInfoFTInstance;
import com.mocentre.tehui.frontend.param.CustomerAddsParam;
import com.mocentre.tehui.frontend.param.CustomerInfoParam;
import com.mocentre.tehui.frontend.service.CustomerInfoManageService;

/**
 * 微信个人中心调用接口
 *
 * @author Created by yukaiji on 2016年12月23日
 */
@Controller
@RequestMapping("/wxa/customerInfo")
public class CustomerInfoWXController extends BaseController {

    @Autowired
    private CustomerInfoManageService customerInfoManageService;
    @Autowired
    private RedisCache                redisCache;

    /**
     * 获取个人信息
     */
    @RequestMapping(value = "getCustomerInfo.htm", method = RequestMethod.GET)
    public void getCustomerInfo(HttpServletRequest request, HttpServletResponse response) {
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        PlainResult<CustomerInfoFTInstance> pr = new PlainResult<CustomerInfoFTInstance>();
        try {
            if (userInfo == null) {
                pr.setErrorMessage("1001", "用户id为空");
            }
            if (pr.isSuccess()) {
                pr = customerInfoManageService.getCustomerInfoById(userInfo.getId(), CommUtil.generateUUID());
            }
        } catch (Exception e) {
            pr.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 获取个人中心地址
     */
    @RequestMapping(value = "getAddress.htm", method = RequestMethod.GET)
    public void address(HttpServletRequest request, HttpServletResponse response) {
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        ListResult<CustomerAddressFTInstance> result = new ListResult<CustomerAddressFTInstance>();
        try {
            if (userInfo == null) {
                result.setErrorMessage("1001", "用户id为空");
            }
            if (result.isSuccess()) {
                result = customerInfoManageService.getAddressByCustomerId(userInfo.getId(), CommUtil.generateUUID());
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 获取个人中心某个地址
     */
    @RequestMapping(value = "editAddress.htm", method = RequestMethod.GET)
    public void editAddress(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        PlainResult<CustomerAddressFTInstance> result = new PlainResult<CustomerAddressFTInstance>();
        try {
            if (userInfo == null) {
                result.setErrorMessage("1001", "用户id为空");
            }
            if (StringUtils.isBlank(id)) {
                result.setErrorMessage("1001", "id为空");
            }
            if (result.isSuccess()) {
                result = customerInfoManageService.getAddressById(userInfo.getId(), Long.valueOf(id),
                        CommUtil.generateUUID());
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 获取是否为首次添加密码
     */
    @RequestMapping(value = "queryPasswordIsNull.htm", method = RequestMethod.GET)
    public void queryPasswordIsNull(HttpServletRequest request, HttpServletResponse response) {
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        PlainResult<String> result = new PlainResult<String>();
        try {
            if (userInfo == null) {
                result.setErrorMessage("1001", "用户id为空");
            }
            if (result.isSuccess()) {
                result = customerInfoManageService.getPasswordIsNull(userInfo.getId(), CommUtil.generateUUID());
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 编辑个人用户信息(添加、修改)
     */
    @RequestMapping(value = "save.htm", method = RequestMethod.POST)
    public void save(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        String param = request.getParameter("customerInfoParam");
        try {
            if (userInfo == null) {
                result.setErrorMessage("1001", "用户id为空");
            }
            if (StringUtils.isBlank(param)) {
                result.setErrorMessage("1001", "参数错误");
            }
            if (result.isSuccess()) {
                JSONObject Obj = JSON.parseObject(param);
                CustomerInfoParam customerInfoParam = JSON.toJavaObject(Obj, CustomerInfoParam.class);
                customerInfoParam.setId(userInfo.getId());
                result = customerInfoManageService.saveCustomerInfo(customerInfoParam);
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 修改密码
     */
    @RequestMapping(value = "editPassword.htm", method = RequestMethod.POST)
    public void editPassword(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        CustomerInfoParam customerInfoParam = super.bindClass(request, CustomerInfoParam.class);
        try {
            if (userInfo == null) {
                result.setErrorMessage("1001", "用户不存在");
            }
            if (customerInfoParam == null) {
                result.setErrorMessage("1001", "参数错误");
            }
            if (result.isSuccess()) {
                customerInfoParam.setId(userInfo.getId());
                customerInfoParam.setRequestId(generageRequestId());
                result = customerInfoManageService.editPassword(customerInfoParam);
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 编辑个人中心地址(添加、修改、修改为默认地址)
     */
    @RequestMapping(value = "saveAddress.htm", method = RequestMethod.POST)
    public void saveAddress(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        String param = request.getParameter("customerAddsParam");
        try {
            if (userInfo == null) {
                result.setErrorMessage("1001", "用户id为空");
            }
            if (StringUtils.isBlank(param)) {
                result.setErrorMessage("1001", "参数错误");
            }
            if (result.isSuccess()) {
                JSONObject Obj = JSON.parseObject(param);
                CustomerAddsParam customerAddsParam = JSON.toJavaObject(Obj, CustomerAddsParam.class);
                customerAddsParam.setCustomerId(userInfo.getId());
                result = customerInfoManageService.saveCustomerAddress(customerAddsParam);
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 删除个人中心地址
     */
    @RequestMapping(value = "delAddress.htm", method = RequestMethod.POST)
    public void delAddress(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        String ids = request.getParameter("idList");
        try {
            if (StringUtils.isBlank(ids)) {
                result.setErrorMessage("1001", "参数错误");
            }
            if (result.isSuccess()) {
                String[] arrIds = ids.split(",");
                List<Long> idList = new ArrayList<Long>();
                for (String id : arrIds) {
                    idList.add(Long.valueOf(id));
                }
                result = customerInfoManageService.deleteCustomerAddressByIdList(idList, CommUtil.generateUUID());
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

}
