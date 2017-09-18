package com.mocentre.gift.front.controller;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.gift.frontend.model.GiftCustomerAddressFTInstance;
import com.mocentre.gift.frontend.model.GiftCustomerFTInstance;
import com.mocentre.gift.frontend.param.GiftCustomerAddsParam;
import com.mocentre.gift.frontend.service.GiftCustomerManageService;
import com.mocentre.gift.frontend.service.GiftLoginManageService;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 礼品平台前端地址接口调用Controller
 * <p>
 * Created by yukaiji on 2017/04/13.
 */
@Controller
@RequestMapping("/giftFront/customer")
public class GiftCustomerFTController extends BaseController {

    @Autowired
    private GiftCustomerManageService giftCustomerManageService;
    @Autowired
    private GiftLoginManageService giftLoginManageService;

    /**
     * 查询展示用户收货地址列表
     */
    @RequestMapping(value = "getAddressList.htm", method = RequestMethod.POST)
    public void queryGiftCategory(HttpServletRequest request, HttpServletResponse response) {
        ListResult<GiftCustomerAddressFTInstance> lr = new ListResult<GiftCustomerAddressFTInstance>();
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        try {
            if (lr.isSuccess()) {
                lr = giftCustomerManageService.getAddressByCustomerId(customerId, generageRequestId());
            }
        } catch (Exception e) {
            lr.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, lr.toJsonString());
    }


    /**
     * 获取个人中心地址列表的某个地址
     */
    @RequestMapping(value = "getAddressById.htm", method = RequestMethod.POST)
    public void editAddress(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        PlainResult<GiftCustomerAddressFTInstance> result = new PlainResult<GiftCustomerAddressFTInstance>();
        try {
            if (StringUtils.isBlank(id)) {
                result.setErrorMessage("1001", "id为空");
            }
            if (result.isSuccess()) {
                result = giftCustomerManageService.getAddressById(customerId, Long.valueOf(id), generageRequestId());
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
        PlainResult<GiftCustomerAddressFTInstance> result = new PlainResult<GiftCustomerAddressFTInstance>();
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        GiftCustomerAddsParam addsParam = super.bindClass(request, GiftCustomerAddsParam.class);
        try {
            if (result.isSuccess()) {
                addsParam.setCustomerId(customerId);
                addsParam.setRequestId(generageRequestId());
                result = giftCustomerManageService.saveCustomerAddress(addsParam);
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
                br = giftCustomerManageService.deleteCustomerAddressByIdList(idList, generageRequestId());
            }
        } catch (Exception e) {
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 获取个人中心用户信息
     */
    @RequestMapping(value = "getCustomerInfo.htm", method = RequestMethod.POST)
    public void getCustomerInfo(HttpServletRequest request, HttpServletResponse response) {
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        PlainResult<GiftCustomerFTInstance> result = new PlainResult<GiftCustomerFTInstance>();
        try {
                result = giftCustomerManageService.getcustomerInfo(customerId, generageRequestId());
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 保存用户手机号
     */
    @RequestMapping(value = "saveTelephone.htm", method = RequestMethod.POST)
    public void saveTelephone(HttpServletRequest request, HttpServletResponse response) {
        String telephone = request.getParameter("telephone");
        String vcode = request.getParameter("vcode");
        // 用户信息
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        // 正确验证码
        String verifycode = (String) request.getSession().getAttribute(SessionKeyConstant.VERIFYCODE);
        BaseResult result = new BaseResult();
        try {
            if (StringUtils.isBlank(telephone)) {
                result.setErrorMessage("1000", "电话号码不能为空");
            }
            if (StringUtils.isBlank(vcode)) {
                result.setErrorMessage("1000", "验证码不能为空");
            }
            if (StringUtils.isBlank(verifycode)) {
                result.setErrorMessage("1000", "验证码已过期");
            }
            if (result.isSuccess()) {
                if (vcode.equals(verifycode)) {
                    result = giftLoginManageService.saveTelephoneByCustomerId(customerId, telephone, generageRequestId());
                } else {
                    result.setErrorMessage("1000", "验证码不正确");
                }
            }
        } catch (Exception e) {
            LOGGER.error("saveTelephone.htm", e);
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }
}
