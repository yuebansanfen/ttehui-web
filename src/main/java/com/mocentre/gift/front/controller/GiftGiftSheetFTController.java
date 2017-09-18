package com.mocentre.gift.front.controller;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.gift.frontend.model.GiftConfirmOrderFTInstance;
import com.mocentre.gift.frontend.model.GiftGiftSheetFTInstance;
import com.mocentre.gift.frontend.param.GiftGiftSheetFTParam;
import com.mocentre.gift.frontend.service.GiftGiftSheetManageService;
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
 * 购物车相关接口
 * Created by 王雪莹 on 2017/4/15.
 */

@Controller
@RequestMapping("/giftFront/giftSheet")
public class GiftGiftSheetFTController extends BaseController {

    @Autowired
    private GiftGiftSheetManageService service;

    /**
     * 添加商品到购物车
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.POST)
    public void add(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        GiftGiftSheetFTParam giftSheetFTParam = this.bindClass(request, GiftGiftSheetFTParam.class);
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        if (giftSheetFTParam.getGoodsId() == null) {
            result.setErrorMessage("1001", "商品id为空");
        }
        if (giftSheetFTParam.getNum() == null) {
            result.setErrorMessage("1001", "商品数量为空");
        }
        if (result.isSuccess()) {
            giftSheetFTParam.setRequestId(generageRequestId());
            result = service.addGiftSheet(customerId, giftSheetFTParam);
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 礼品单列表
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "queryList.htm", method = RequestMethod.POST)
    public void queryList(HttpServletRequest request, HttpServletResponse response) {
        ListResult<GiftGiftSheetFTInstance> result = new ListResult<>();
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        result = service.queryList(customerId, generageRequestId());
        super.printJson(response, result.toJsonString());
    }

    /**
     * 更新礼品数量
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "changeNum.htm", method = RequestMethod.POST)
    public void changeNum(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        String idStr = request.getParameter("id");
        String numStr = request.getParameter("num");
        if (StringUtils.isBlank(idStr) || StringUtils.isBlank(numStr)) {
            result.setErrorMessage("999", "参数错误");
        }
        if (result.isSuccess()) {
            result = service.changeNum(customerId, Long.parseLong(idStr), Integer.parseInt(numStr), generageRequestId());
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 删除礼品单礼品
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        BaseResult result = new BaseResult();
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        String idStr = request.getParameter("id");
        if (StringUtils.isBlank(idStr)) {
            result.setErrorMessage("999", "参数错误");
        }
        if (result.isSuccess()) {
            result = service.delete(customerId, Long.parseLong(idStr), generageRequestId());
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 提交礼品单到确认订单页
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "buy.htm", method = RequestMethod.POST)
    public void buy(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<GiftConfirmOrderFTInstance> result = new PlainResult<>();
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        String idListStr = request.getParameter("idList");
        if (StringUtils.isBlank(idListStr)) {
            result.setErrorMessage("999", "参数错误");
        }
        if (result.isSuccess()) {
            List<Long> idList = new ArrayList<Long>();
            String[] ids = idListStr.split(",");
            for (String id : ids) {
                idList.add(Long.valueOf(id));
            }
            result = service.buy(customerId, idList, generageRequestId());
        }
        super.printJson(response, result.toJsonString());
    }


    /**
     * 获取购物单数量
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "querySheetNum.htm", method = RequestMethod.POST)
    public void querySheetNum(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<Long> result = new PlainResult<Long>();
        Long customerId = (Long) request.getSession().getAttribute(SessionKeyConstant.GIFTCUSTOMER_ID);
        result = service.querySheetNum(customerId, generageRequestId());
        super.printJson(response, result.toJsonString());
    }
}
