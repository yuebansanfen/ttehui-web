package com.mocentre.tehui.front.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.mocentre.tehui.common.SystemConfig;
import com.mocentre.tehui.common.constant.ConfigConstant;
import com.mocentre.tehui.common.upyun.YunFormUploader;
import com.mocentre.tehui.common.upyun.YunParams;
import com.mocentre.tehui.common.util.CookieUtil;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.CustomerAddressFTInstance;
import com.mocentre.tehui.frontend.model.CustomerInfoFTInstance;
import com.mocentre.tehui.frontend.param.CustomerAddsParam;
import com.mocentre.tehui.frontend.param.CustomerInfoParam;
import com.mocentre.tehui.frontend.service.CustomerInfoManageService;

/**
 * 个人中心前台调用接口
 *
 * @author Created by yukaiji on 2016年12月23日
 */
@Controller
@RequestMapping("/front/customerInfo")
public class CustomerInfoFTController extends BaseController {

    @Autowired
    private CustomerInfoManageService customerInfoManageService;
    @Autowired
    private RedisCache                redisCache;

    /**
     * 获取个人信息
     */
    @RequestMapping(value = "getCustomerInfo.htm", method = RequestMethod.POST)
    public void getCustomerInfo(HttpServletResponse response) {
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        PlainResult<CustomerInfoFTInstance> pr = new PlainResult<CustomerInfoFTInstance>();
        try {
            if (userInfo == null) {
                pr.setErrorMessage("1001", "用户id为空");
            }
            if (pr.isSuccess()) {
                pr = customerInfoManageService.getCustomerInfoById(userInfo.getId(), generageRequestId());
            }
        } catch (Exception e) {
            pr.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 添加、修改个人用户信息
     */
    @RequestMapping(value = "save.htm", method = RequestMethod.POST)
    public void save(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        CustomerInfoParam customerInfoParam = super.bindClass(request, CustomerInfoParam.class);
        try {
            if (userInfo == null) {
                result.setErrorMessage("1001", "用户不存在");
            }
            if (result.isSuccess()) {
                customerInfoParam.setId(userInfo.getId());
                customerInfoParam.setRequestId(generageRequestId());
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
        boolean paramValid = StringUtils.isBlank(customerInfoParam.getNewPassword())
                || StringUtils.isBlank(customerInfoParam.getRepeatPassword());
        if (paramValid) {
            result.setErrorMessage("1000", "参数错误");
        }
        if (userInfo == null) {
            result.setErrorMessage("1001", "用户不存在");
        }
        if (!customerInfoParam.getNewPassword().equals(customerInfoParam.getRepeatPassword())) {
            result.setErrorMessage("1002", "两次输入密码不一致");
        }
        try {
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
     * 获取个人中心地址列表
     */
    @RequestMapping(value = "getAddress.htm", method = RequestMethod.POST)
    public void getAddress(HttpServletResponse response) {
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        ListResult<CustomerAddressFTInstance> lr = new ListResult<CustomerAddressFTInstance>();
        try {
            if (userInfo == null) {
                lr.setErrorMessage("1001", "用户id为空");
            }
            if (lr.isSuccess()) {
                lr = customerInfoManageService.getAddressByCustomerId(userInfo.getId(), generageRequestId());
            }
        } catch (Exception e) {
            lr.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, lr.toJsonString());
    }

    /**
     * 获取个人中心地址列表的某个地址
     */
    @RequestMapping(value = "editAddress.htm", method = RequestMethod.POST)
    public void editAddress(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        PlainResult<CustomerAddressFTInstance> result = new PlainResult<CustomerAddressFTInstance>();
        try {
            if (userInfo == null) {
                result.setErrorMessage("1001", "用户不存在");
            }
            if (StringUtils.isBlank(id)) {
                result.setErrorMessage("1001", "id为空");
            }
            if (result.isSuccess()) {
                result = customerInfoManageService.getAddressById(userInfo.getId(), Long.valueOf(id),
                        generageRequestId());
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
        PlainResult<CustomerAddressFTInstance> result = new PlainResult<CustomerAddressFTInstance>();
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        CustomerAddsParam customerAddsParam = super.bindClass(request, CustomerAddsParam.class);
        try {
            if (userInfo == null) {
                result.setErrorMessage("1001", "用户不存在");
            }
            if (result.isSuccess()) {
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
        BaseResult br = new BaseResult();
        String ids = request.getParameter("idList");
        try {
            if (StringUtils.isBlank(ids)) {
                br.setErrorMessage("1001", "参数错误");
            }
            if (br.isSuccess()) {
                String[] arrIds = ids.split(",");
                List<Long> idList = new ArrayList<Long>();
                for (String id : arrIds) {
                    idList.add(Long.valueOf(id));
                }
                br = customerInfoManageService.deleteCustomerAddressByIdList(idList, generageRequestId());
            }
        } catch (Exception e) {
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 上传图片
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "uploadImg.htm", method = RequestMethod.POST)
    public void uploadImg(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<Map<String, Object>> result = new PlainResult<Map<String, Object>>();
        Map<String, Object> resMap = new HashMap<String, Object>();
        result.setRequestId(generageRequestId());
        try {
            String cxtPath = request.getContextPath();
            String path = "https://" + request.getServerName() + cxtPath;
            Map<String, Object> paramsMap = new LinkedHashMap<String, Object>();
            Long time = new Date().getTime();
            paramsMap.put(YunParams.SAVE_KEY, "/uploads/{year}/{mon}/{day}/" + time + "_{random}{.suffix}");
            paramsMap.put(YunParams.ALLOW_FILE_TYPE, "gif,jpeg,jpg,png");
            paramsMap.put(YunParams.RETURN_URL, path + "/common/upyun/frontUploadImgReturn");
            paramsMap.put(YunParams.CONTENT_LENGTH_RANGE, "0,1024000");
            String bucketName = SystemConfig.INSTANCE.getValue(ConfigConstant.UPYUN_BUCKET);
            String apiKey = SystemConfig.INSTANCE.getValue(ConfigConstant.UPYUN_API_KEY);
            YunFormUploader upYun = new YunFormUploader(bucketName, apiKey);
            String policy = upYun.getPolicy(paramsMap);
            String signature = upYun.getSignature(policy);
            String action = upYun.getAction();
            resMap.put("signature", signature);
            resMap.put("policy", policy);
            resMap.put("action", action);
            result.setData(resMap);
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 退出登录
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "logout.htm", method = RequestMethod.POST)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        try {
            String cookieDomain = SystemConfig.INSTANCE.getValue(ConfigConstant.TL_COOKIE_DOMAIN);
            redisCache.clearLoginInfo();
            CookieUtil.removeAllCookie(request, response, cookieDomain);
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

}
