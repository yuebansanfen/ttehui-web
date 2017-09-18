/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.shop.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.ShopManageService;
import com.mocentre.tehui.backend.model.ShopInstance;
import com.mocentre.tehui.backend.param.ShopParam;
import com.mocentre.tehui.backend.param.ShopQueryParam;
import com.mocentre.tehui.common.util.CommUtil;

/**
 * 类ShopService.java的实现描述：店铺service
 *
 * @author yukaiji 2016年11月14日
 */
@Component
public class ShopService {

    @Autowired
    private ShopManageService shopManageService;

    /**
     * 管理员查询所有的店铺
     *
     * @return
     */
    public ListJsonResult<ShopInstance> queryShopPage(ShopQueryParam shopQuery) {
        ListJsonResult<ShopInstance> br = new ListJsonResult<ShopInstance>();
        ListJsonResult<ShopInstance> result = shopManageService.queryPage(shopQuery);
        if (result == null) {
            br.setErrorMessage("99", "查询失败");
            return br;
        }
        if (!result.isSuccess()) {
            br.setErrorMessage("100", "查询失败");
            return br;
        }
        br = result;
        return br;
    }

    /**
     * 商户查询自己的店铺信息
     *
     * @param id
     * @return
     */
    public ShopInstance queryById(Long id) {
        ShopInstance shopIns = null;
        String requestId = CommUtil.generateUUID();
        PlainResult<ShopInstance> result = shopManageService.getShop(id, requestId);
        if (result != null) {
            if (result.isSuccess()) {
                shopIns = result.getData();
            }
        }
        return shopIns;
    }

    /**
     * 商户添加店铺信息
     *
     * @param shopParam
     * @return
     */
    public PlainResult<Long> useradd(ShopParam shopParam) {
        PlainResult<Long> pr = new PlainResult<Long>();
        shopParam.setIs_admin(false);
        shopParam.setLevel(1);
        PlainResult<Long> result = shopManageService.addShop(shopParam);
        if (result != null) {
            if (!result.isSuccess()) {
                pr.setErrorMessage("100", "添加失败");
            } else {
                pr = result;
            }
        } else {
            pr.setErrorMessage("100", "添加失败");
        }
        return pr;
    }

    public BaseResult adminadd(ShopParam shopParam) {
        BaseResult br = new BaseResult();
        shopParam.setIs_admin(true);
        BaseResult result = shopManageService.addShop(shopParam);
        if (result != null) {
            if (!result.isSuccess()) {
                br.setErrorMessage("100", "添加失败");
            } else {
                br = result;
            }
        } else {
            br.setErrorMessage("100", "添加失败");
        }
        return br;
    }

    /**
     * 商户修改自己的店铺信息
     *
     * @param shopParam
     * @return
     */
    public BaseResult edit(ShopParam shopParam) {
        BaseResult br = new BaseResult();
        BaseResult result = shopManageService.editShop(shopParam);
        if (result != null) {
            if (!result.isSuccess()) {
                br.setErrorMessage("100", "修改失败");
            } else {
                br = result;
            }
        } else {
            br.setErrorMessage("100", "修改失败");
        }
        return br;
    }

    /**
     * 删除店铺
     *
     * @param idList
     */
    public BaseResult delete(String idList) {
        BaseResult br = new BaseResult();
        String requestId = CommUtil.generateUUID();
        String[] sIdList = idList.split(",");
        List<Long> lIdList = new ArrayList<Long>();
        for (String id : sIdList) {
            lIdList.add(Long.valueOf(id));
        }
        BaseResult result = shopManageService.deleteShop(lIdList, requestId);
        if (result != null) {
            if (!result.isSuccess()) {
                br.setErrorMessage("100", "删除失败");
            } else {
                br = result;
            }
        } else {
            br.setErrorMessage("100", "删除失败");
        }
        return br;
    }

    public BaseResult examine(Long id, String audit_status) {
        BaseResult br = new BaseResult();
        String requestId = CommUtil.generateUUID();
        BaseResult result = shopManageService.examineShop(id, audit_status, requestId);
        if (result != null) {
            if (!result.isSuccess()) {
                br.setErrorMessage("100", "操作失败");
            } else {
                br = result;
            }
        } else {
            br.setErrorMessage("100", "操作失败");
        }
        return br;
    }

    public BaseResult operation(Long id, String buss_status) {
        BaseResult br = new BaseResult();
        String requestId = CommUtil.generateUUID();
        BaseResult result = shopManageService.operationShop(id, buss_status, requestId);
        if (result != null) {
            if (!result.isSuccess()) {
                br.setErrorMessage("100", "操作失败");
            } else {
                br = result;
            }
        } else {
            br.setErrorMessage("100", "操作失败");
        }
        return br;
    }
}
