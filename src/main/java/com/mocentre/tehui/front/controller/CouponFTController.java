package com.mocentre.tehui.front.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.CouponDetailFTInstance;
import com.mocentre.tehui.frontend.model.CouponReceiveFTInstance;
import com.mocentre.tehui.frontend.model.CustomerInfoFTInstance;
import com.mocentre.tehui.frontend.param.CashCouponParam;
import com.mocentre.tehui.frontend.param.CashGoodsParam;
import com.mocentre.tehui.frontend.param.CashUseCouponParam;
import com.mocentre.tehui.frontend.param.CountCouponParam;
import com.mocentre.tehui.frontend.param.CouponBindParam;
import com.mocentre.tehui.frontend.param.CouponQueryParam;
import com.mocentre.tehui.frontend.service.CouponManageService;

/**
 * 优惠券前台调用接口
 *
 * @author Created by yukaiji on 2016年12月23日
 */
@Controller
@RequestMapping("/front/coupon")
public class CouponFTController extends BaseController {

    @Autowired
    private CouponManageService couponManageService;
    @Autowired
    private RedisCache          redisCache;

    /**
     * 获取优惠券信息列表
     */
    @RequestMapping(value = "getCouponList.htm", method = RequestMethod.POST)
    public void getCouponList(HttpServletRequest request, HttpServletResponse response) {
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        ListResult<CouponDetailFTInstance> lr = new ListResult<CouponDetailFTInstance>();
        CouponQueryParam couponParam = bindClass(request, CouponQueryParam.class);
        try {
            if (userInfo == null) {
                lr.setErrorMessage("1001", "用户不存在");
            }
            boolean paramValid = StringUtils.isBlank(couponParam.getType())
                    || ("1".equals(couponParam.getType()) && "2".equals(couponParam.getType()) && "3"
                            .equals(couponParam.getType()));
            if (paramValid) {
                lr.setErrorMessage("1000", "参数错误");
            }
            if (lr.isSuccess()) {
                Long cumId = userInfo.getId();
                couponParam.setCustomerId(cumId);
                lr = couponManageService.queryCouponDeatilByCustomerId(couponParam);
            }
        } catch (Exception e) {
            lr.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, lr.toJsonString());
    }

    /**
     * 查询可用优惠券数量
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "count_num.htm", method = RequestMethod.POST)
    public void countCouponNum(HttpServletRequest request, HttpServletResponse response) {
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        CountCouponParam couponParam = super.bindClass(request, CountCouponParam.class);
        couponParam.setRequestId(generageRequestId());
        PlainResult<Integer> pr = new PlainResult<Integer>();
        String comefrom = couponParam.getComefrom();
        Integer buyNum = couponParam.getBuyNum();
        Long goodsId = couponParam.getGoodsId();
        String stcode = couponParam.getStandardCode();
        String orderNum = couponParam.getOrderNum();
        boolean paramValid = StringUtils.isBlank(comefrom)
                || !("1".equals(comefrom) || "2".equals(comefrom) || "3".equals(comefrom));
        if (paramValid) {
            pr.setErrorMessage("1000", "参数错误");
        }
        paramValid = "2".equals(comefrom) && (StringUtils.isBlank(stcode) || buyNum == null || goodsId == null);
        if (paramValid) {
            pr.setErrorMessage("1000", "参数错误");
        }
        paramValid = "3".equals(comefrom) && StringUtils.isBlank(orderNum);
        if (paramValid) {
            pr.setErrorMessage("1000", "参数错误");
        }
        if (userInfo == null) {
            pr.setErrorMessage("1001", "用户不存在");
        }
        try {
            if (pr.isSuccess()) {
                Long cumId = userInfo.getId();
                couponParam.setCustomerId(cumId);
                pr = couponManageService.countUsedCoupon(couponParam);
            }
        } catch (Exception e) {
            pr.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 查询用户当前收银台可使用和不使用过的优惠券
     */
    @RequestMapping(value = "cash_coupon.htm", method = RequestMethod.POST)
    public void cashCoupon(HttpServletRequest request, HttpServletResponse response) {

        ListResult<CouponDetailFTInstance> lr = new ListResult<CouponDetailFTInstance>();
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        String cashGoodsJson = request.getParameter("cashGoodsJsonArr");
        String type = request.getParameter("type");
        String startStr = request.getParameter("start");
        String lengthStr = request.getParameter("length");
        Integer start = StringUtils.isBlank(startStr) ? 1 : Integer.parseInt(startStr);
        Integer length = StringUtils.isBlank(lengthStr) ? 10 : Integer.parseInt(lengthStr);
        boolean paramValid = StringUtils.isBlank(cashGoodsJson) || StringUtils.isBlank(type);
        if (paramValid) {
            lr.setErrorMessage("1000", "参数错误");
        }
        if (userInfo == null) {
            lr.setErrorMessage("1001", "用户不存在");
        }
        try {
            if (lr.isSuccess()) {
                List<CashGoodsParam> cashGoodsList = new ArrayList<CashGoodsParam>();
                JSONArray cashGoodsJArr = JSON.parseArray(cashGoodsJson);
                for (int i = 0; i < cashGoodsJArr.size(); i++) {
                    JSONObject jObj = cashGoodsJArr.getJSONObject(i);
                    CashGoodsParam goodsParam = JSON.toJavaObject(jObj, CashGoodsParam.class);
                    cashGoodsList.add(goodsParam);
                }
                CashCouponParam cashCouponParam = new CashCouponParam();
                cashCouponParam.setCustomerId(userInfo.getId());
                cashCouponParam.setCashGoodsList(cashGoodsList);
                cashCouponParam.setRequestId(generageRequestId());
                cashCouponParam.setType(type);
                cashCouponParam.setStart(start);
                cashCouponParam.setLength(length);
                lr = couponManageService.queryUsedCoupon(cashCouponParam);
            }
        } catch (Exception e) {
            LOGGER.error("cashCoupon", e);
            lr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, lr.toJsonString());
    }

    /**
     * 绑定优惠码
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "bind_coupon.htm", method = RequestMethod.POST)
    public void bindCoupon(HttpServletRequest request, HttpServletResponse response) {

        PlainResult<CouponDetailFTInstance> pr = new PlainResult<CouponDetailFTInstance>();
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        String couponCode = request.getParameter("couponCode");
        if (StringUtils.isBlank(couponCode)) {
            pr.setErrorMessage("1000", "参数错误");
        }
        if (userInfo == null) {
            pr.setErrorMessage("1001", "用户不存在");
        }
        try {
            Long customerId = userInfo.getId();
            if (pr.isSuccess()) {
                pr = couponManageService.bindCoupon(couponCode, customerId, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("bindCoupon", e);
            pr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 收银台绑定优惠码
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "bind_cash_coupon.htm", method = RequestMethod.POST)
    public void bindCashCoupon(HttpServletRequest request, HttpServletResponse response) {

        PlainResult<CouponDetailFTInstance> pr = new PlainResult<CouponDetailFTInstance>();
        String couponCode = request.getParameter("couponCode");
        String cashGoodsJson = request.getParameter("cashGoodsJsonArr");
        try {
            if (StringUtils.isBlank(couponCode) || StringUtils.isBlank(cashGoodsJson)) {
                pr.setErrorMessage("1000", "参数错误");
            }
            List<CashGoodsParam> cashGoodsList = new ArrayList<CashGoodsParam>();
            JSONArray cashGoodsJArr = JSON.parseArray(cashGoodsJson);
            for (int i = 0; i < cashGoodsJArr.size(); i++) {
                JSONObject jObj = cashGoodsJArr.getJSONObject(i);
                CashGoodsParam goodsParam = JSON.toJavaObject(jObj, CashGoodsParam.class);
                cashGoodsList.add(goodsParam);
            }
            if (cashGoodsList.size() == 0) {
                pr.setErrorMessage("1000", "参数错误");
            }
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            if (userInfo == null) {
                pr.setErrorMessage("1001", "用户不存在");
            }
            Long customerId = userInfo.getId();
            if (pr.isSuccess()) {
                CouponBindParam couponParam = new CouponBindParam();
                couponParam.setCustomerId(customerId);
                couponParam.setCode(couponCode);
                couponParam.setCashGoodsList(cashGoodsList);
                pr = couponManageService.bindCashCoupon(couponParam);
            }
        } catch (Exception e) {
            LOGGER.error("bind_cash_coupon", e);
            pr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 领取的优惠券列表
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "coupon_list.htm", method = RequestMethod.POST)
    public void couponList(HttpServletRequest request, HttpServletResponse response) {

        ListResult<CouponReceiveFTInstance> lr = new ListResult<CouponReceiveFTInstance>();
        String goodsIdStr = request.getParameter("goodsId");
        boolean paramValid = StringUtils.isBlank(goodsIdStr);
        if (paramValid) {
            lr.setErrorMessage("1000", "参数错误");
        }
        try {
            Long goodsId = Long.parseLong(goodsIdStr);
            if (lr.isSuccess()) {
                lr = couponManageService.getReceiveCouponList(goodsId, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("couponList", e);
            lr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, lr.toJsonString());
    }

    /**
     * 领取优惠券
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "receive_coupon.htm", method = RequestMethod.POST)
    public void receiveCoupon(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
        String couponId = request.getParameter("couponId");
        if (StringUtils.isBlank(couponId)) {
            br.setErrorMessage("1000", "参数错误");
        }
        if (userInfo == null) {
            br.setErrorMessage("1001", "用户不存在");
        }
        try {
            Long customerId = userInfo.getId();
            if (br.isSuccess()) {
                br = couponManageService.receiveCoupon(Long.parseLong(couponId), customerId, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("receiveCoupon", e);
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 使用优惠券后，计算订单价格
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "use_coupon.htm", method = RequestMethod.POST)
    public void orderUseCoupon(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<String> pr = new PlainResult<String>();
        String couponCode = request.getParameter("couponCode");
        String cashGoodsJson = request.getParameter("cashGoodsJsonArr");
        try {
            if (cashGoodsJson == null) {
                pr.setErrorMessage("1000", "参数错误");
            }
            List<CashGoodsParam> cashGoodsList = new ArrayList<CashGoodsParam>();
            JSONArray cashGoodsJArr = JSON.parseArray(cashGoodsJson);
            for (int i = 0; i < cashGoodsJArr.size(); i++) {
                JSONObject jObj = cashGoodsJArr.getJSONObject(i);
                CashGoodsParam goodsParam = JSON.toJavaObject(jObj, CashGoodsParam.class);
                cashGoodsList.add(goodsParam);
            }
            if (cashGoodsList.size() == 0) {
                pr.setErrorMessage("1000", "参数错误");
            }
            CustomerInfoFTInstance userInfo = redisCache.currentLoginUser();
            if (userInfo == null) {
                pr.setErrorMessage("1001", "用户不存在");
            }
            Long customerId = userInfo.getId();
            if (pr.isSuccess()) {
                CashUseCouponParam useCouponParam = new CashUseCouponParam();
                useCouponParam.setCustomerId(customerId);
                useCouponParam.setCashGoodsList(cashGoodsList);
                useCouponParam.setCouponCode(couponCode);
                pr = couponManageService.orderUseCoupon(useCouponParam);
            }
        } catch (Exception e) {
            LOGGER.error("use_coupon", e);
            pr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, pr.toJsonString());
    }

}
