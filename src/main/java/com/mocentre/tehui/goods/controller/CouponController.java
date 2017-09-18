package com.mocentre.tehui.goods.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.backend.model.CategoryInstance;
import com.mocentre.tehui.backend.model.CouponDetailInstance;
import com.mocentre.tehui.backend.model.CouponInstance;
import com.mocentre.tehui.backend.param.CouponDetailQueryParam;
import com.mocentre.tehui.backend.param.CouponParam;
import com.mocentre.tehui.backend.param.CouponQueryParam;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.goods.service.CouponService;

/**
 * 优惠券controller Created by yukaiji on 2016/11/25.
 */
@Controller
@RequestMapping("/goods/coupon")
public class CouponController extends BaseController {

    @Autowired
    private CouponService couponService;

    /**
     * 跳转到首页
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
     * 跳转到优惠券详细页
     * 
     * @param request
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "view.htm", method = RequestMethod.GET)
    public String view(Long id, Model model, HttpServletRequest request) {
        try {
            Object shopid = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            PlainResult<CouponInstance> pr = couponService.queryCouponById(id, Long.valueOf(shopid.toString()));
            if (pr.isSuccess()) {
                model.addAttribute("coupon", pr.getData());
            } else {
                model.addAttribute("errorMsg", "访问失败");
                return getErrorIndex();
            }
        } catch (Exception e) {
            model.addAttribute("errorMsg", "系统异常");
            LOGGER.error("view", e);
        }
        model.addAttribute("id", id);
        return getNameSpace() + "view";
    }

    /**
     * 跳转到添加页面
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.GET)
    public String add(Model model, HttpServletRequest request, HttpServletResponse response) {
        List<CategoryInstance> cyList = couponService.queryCategory();
        model.addAttribute("cyList", cyList);
        return getNameSpace() + "add";
    }

    /**
     * 跳转到修改页面
     * 
     * @param request
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "edit.htm", method = RequestMethod.GET)
    public String edit(Long id, Model model, HttpServletRequest request) {
        try {
            Object shopid = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            PlainResult<Map<String, Object>> pr = couponService.prviewEdit(id, Long.parseLong(shopid.toString()));
            if (pr.isSuccess()) {
                Map<String, Object> prMap = pr.getData();
                model.addAttribute("coupon", prMap.get("coupon"));
                model.addAttribute("cyList", prMap.get("categoryList"));
                model.addAttribute("goodsList", prMap.get("goodsList"));
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
     * 分页查询
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "query.htm", method = RequestMethod.POST)
    public void query(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<CouponInstance> lr = new ListJsonResult<CouponInstance>();
        Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
        try {
            CouponQueryParam couponQueryParam = super.bindDTParamClass(request, CouponQueryParam.class);
            couponQueryParam.setRequestId(generageRequestId());
            lr = couponService.queryCouponPage(couponQueryParam, shopId);
        } catch (Exception e) {
            LOGGER.error("query", e);
            lr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, lr.toJsonString());
    }

    /**
     * 分页查询
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "queryDetail.htm", method = RequestMethod.POST)
    public void queryDetail(Long id, HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<CouponDetailInstance> br = new ListJsonResult<CouponDetailInstance>();
        try {
            CouponDetailQueryParam couponDetailParam = bindClass(request, CouponDetailQueryParam.class);
            couponDetailParam.setRequestId(generageRequestId());
            br = couponService.queryCouponDetailPage(couponDetailParam, id);
        } catch (Exception e) {
            LOGGER.error("query", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 添加优惠券
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "addCoupon.htm", method = RequestMethod.POST)
    public void addcoupon(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            Object shopid = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            if (shopid != null) {
                CouponParam coupon = bindClass(request, CouponParam.class);
                coupon.setRequestId(generageRequestId());
                coupon.setShopId(Long.valueOf(shopid.toString()));
                br = couponService.addCoupon(coupon);
            } else {
                br.setErrorMessage("100", "添加失败");
            }
        } catch (Exception e) {
            LOGGER.error("addcoupon", e);
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 修改优惠券
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "editCoupon.htm", method = RequestMethod.POST)
    public void editCoupon(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            Object shopid = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            if (shopid != null) {
                CouponParam coupon = bindClass(request, CouponParam.class);
                coupon.setRequestId(generageRequestId());
                coupon.setShopId(Long.valueOf(shopid.toString()));
                br = couponService.editCoupon(coupon);
            } else {
                br.setErrorMessage("1001", "修改失败");
            }
        } catch (Exception e) {
            LOGGER.error("editDiscover", e);
        }
        super.printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String idList = request.getParameter("idList");
        br = couponService.delete(idList);
        super.printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "exportCoupon.htm", method = RequestMethod.POST)
    public void exportCoupon(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            String couponId = request.getParameter("couponId");
            //获取服务器路径
            String dir = request.getSession().getServletContext().getRealPath("/");
            if (StringUtils.isEmpty(couponId)) {
                br.setErrorMessage("1001", "优惠券信息为空！");
            }
            if (br.isSuccess()) {
                long now = System.currentTimeMillis();
                String userdir = dir + "couponFile/" + now + "/";
                String excelDir = couponService.uploadCoupon(Long.parseLong(couponId), userdir);
                if (!org.apache.commons.lang.StringUtils.isBlank(excelDir)) {
                    response.setContentType("application/ms-excel");
                    response.setHeader("Content-Disposition", "attachment;Filename=coupon" + now + ".xlsx");
                    OutputStream out = null;
                    FileInputStream fis = null;
                    try {
                        out = response.getOutputStream();
                        fis = new FileInputStream(excelDir);
                        // 写出流信息
                        int i = 0;
                        while ((i = fis.read()) != -1) {
                            out.write(i);
                        }
                    } catch (IOException e) {
                        br.setErrorMessage("999", "下载失败");
                        LOGGER.error("exportOrder", e);
                    } finally {
                        if (fis != null) {
                            fis.close();
                        }
                        if (out != null) {
                            out.flush();
                            out.close();
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("exportOrder", e);
        }
    }
}
