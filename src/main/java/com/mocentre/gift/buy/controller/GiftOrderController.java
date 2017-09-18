package com.mocentre.gift.buy.controller;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.PlainResult;
import com.mocentre.gift.backend.model.GiftOrderInfoInstance;
import com.mocentre.gift.backend.model.GiftOrderPageInstance;
import com.mocentre.gift.backend.param.GiftBillParam;
import com.mocentre.gift.backend.param.GiftExpParam;
import com.mocentre.gift.backend.param.GiftOrderDetailParam;
import com.mocentre.gift.backend.param.GiftOrderParam;
import com.mocentre.gift.backend.service.GiftOrderMangeService;
import com.mocentre.gift.buy.controller.service.GiftOrderService;
import com.mocentre.tehui.core.controller.BaseController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * 电子礼品-订单：controller
 * Created by 王雪莹 on 2017/4/11.
 */
@Controller
@RequestMapping("/giftbuy/order")
public class GiftOrderController extends BaseController {

    @Autowired
    private GiftOrderMangeService   orderMangeService;
    @Autowired
    private GiftOrderService        orderService;

    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response) {
        return getNameSpace() + "index";
    }

    @RequestMapping(value = "queryPaged.htm", method = RequestMethod.POST)
    public void queryPaged(HttpServletRequest request, HttpServletResponse response) {

        ListJsonResult<GiftOrderPageInstance> br = new ListJsonResult<GiftOrderPageInstance>();
        try {
            if (br.isSuccess()) {
                GiftOrderParam orderQueryParam = bindClass(request, GiftOrderParam.class);
                br = orderMangeService.queryPage(orderQueryParam);
            }
            super.printJson(response, br.toJsonString());
        } catch (Exception e) {
            LOGGER.error("queryPaged", e);
        }
    }

    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    public void deletet(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        try {
            String id = request.getParameter("id");
            if (!StringUtils.isBlank(id)) {
                br = orderMangeService.deleteById(Long.parseLong(id), generageRequestId());
            } else {
                br.setErrorMessage("1001", "id为空");
            }
        } catch (Exception e) {
            LOGGER.error("delete", e);
            br.setErrorMessage("98", "删除失败");
        }
        printJson(response, br.toJsonString());
    }


    @RequestMapping(value = "detail.htm", method = RequestMethod.GET)
    public String detail(Long id, Model model, HttpServletRequest request) {
        try {
            PlainResult<GiftOrderInfoInstance> pr = orderMangeService.queryOrderDetail(id, generageRequestId());
            if (!pr.isSuccess()) {
                model.addAttribute("errorMsg", "查看详情失败");
                return getErrorIndex();
            }
            model.addAttribute("order", pr.getData());
        } catch (Exception e) {
            LOGGER.error("detail", e);
        }
        return getNameSpace() + "detail";
    }

    @RequestMapping(value = "editSend.htm", method = RequestMethod.POST)
    public void editSend(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        try {
            GiftExpParam giftExpParam = bindClass(request, GiftExpParam.class);
            giftExpParam.setRequestId(generageRequestId());
            if (request.getParameter("orderId") == null) {
                br.setErrorMessage("100", "提交失败");
            }
            if (br.isSuccess()) {
                br = orderMangeService.editSendOrder(giftExpParam);
            }
        } catch (Exception e) {
            LOGGER.error("editSend", e);
            e.printStackTrace();
            br.setErrorMessage("99", "系统异常");
        }
        printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "editStatus.htm", method = RequestMethod.POST)
    public void editStatus(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        try {
            String orderId = request.getParameter("orderId");
            String status = request.getParameter("status");
            if (orderId == null) {
                br.setErrorMessage("100", "提交失败");
            }
            if (br.isSuccess()) {
                br = orderMangeService.editStatus(Long.parseLong(orderId), status, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("editSend", e);
            e.printStackTrace();
            br.setErrorMessage("99", "系统异常");
        }
        printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "editAddress.htm", method = RequestMethod.POST)
    public void editAddress(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        try {
            String orderId = request.getParameter("orderId");
            String recipient = request.getParameter("recipient");
            String telephone = request.getParameter("telephone");
            String address = request.getParameter("address");
            if (orderId == null) {
                br.setErrorMessage("100", "提交失败");
            }
            if (br.isSuccess()) {
                br = orderMangeService.editAddress(Long.parseLong(orderId), recipient, telephone, address, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("editSend", e);
            e.printStackTrace();
            br.setErrorMessage("99", "系统异常");
        }
        printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "editBill.htm", method = RequestMethod.POST)
    public void editBill(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            GiftBillParam bill = bindClass(request, GiftBillParam.class);
            Long orderId = bill.getOrderId();
            if (orderId == null) {
                br.setErrorMessage("100", "提交失败");
            }
            if (br.isSuccess()) {
                br = orderMangeService.editBill(bill);
            }
        } catch (Exception e) {
            LOGGER.error("editBill", e);
            e.printStackTrace();
            br.setErrorMessage("99", "系统异常");
        }
        printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "editDetail.htm", method = RequestMethod.POST)
    public void editDetail(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            GiftOrderDetailParam detail = bindClass(request, GiftOrderDetailParam.class);
            Long orderId = detail.getOrderId();
            Long id = detail.getId();
            if (orderId == null) {
                br.setErrorMessage("100", "提交失败");
            }
            if (br.isSuccess()) {
                br = orderMangeService.editDetail(detail);
            }
        } catch (Exception e) {
            LOGGER.error("editDetail", e);
            e.printStackTrace();
            br.setErrorMessage("99", "系统异常");
        }
        printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "delDetail.htm", method = RequestMethod.POST)
    public void delDetail(HttpServletRequest request, HttpServletResponse response) {

        BaseResult br = new BaseResult();
        try {
            String id = request.getParameter("id");
            if (!StringUtils.isBlank(id)) {
                br = orderMangeService.deleteDetailById(Long.parseLong(id), generageRequestId());
            } else {
                br.setErrorMessage("1001", "id为空");
            }
        } catch (Exception e) {
            LOGGER.error("delete", e);
            br.setErrorMessage("98", "删除失败");
        }
        printJson(response, br.toJsonString());
    }

    @RequestMapping(value = "uploadOrder.htm", method = RequestMethod.POST)
    public void exeUploadFile(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        String orderNum = request.getParameter("orderNum");
        //获取服务器路径
        String dir = request.getSession().getServletContext().getRealPath("/");
        //获取文件路径
        String userdir = dir + "orderFile/" + orderNum + "/";
        try {
            String excelDir = orderService.uploadOrder(Long.valueOf(id), userdir, orderNum, generageRequestId());
            if (!StringUtils.isBlank(excelDir)) {
                response.setContentType("application/ms-excel");
                response.setHeader("Content-Disposition", "attachment;Filename=" + orderNum + ".xlsx");
                OutputStream out;
                try {
                    out = response.getOutputStream();
                    FileInputStream fis = new FileInputStream(excelDir);
                    // 写出流信息
                    int i = 0;
                    while ((i = fis.read()) != -1) {
                        out.write(i);
                    }
                    fis.close();
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    br.setErrorMessage("999", "下载失败");
                    LOGGER.error("exeUpload", e);
                }
            }
        } catch (Exception e) {
            br.setErrorMessage("999", "下载失败");
            LOGGER.error("exeUpload", e);
        }
    }
}
