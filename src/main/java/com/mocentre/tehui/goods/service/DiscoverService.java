/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.goods.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.CategoryManageService;
import com.mocentre.tehui.DiscoverManageService;
import com.mocentre.tehui.backend.model.CategoryInstance;
import com.mocentre.tehui.backend.model.DiscoverAllMsgInstance;
import com.mocentre.tehui.backend.model.DiscoverInstance;
import com.mocentre.tehui.backend.param.DiscoverParam;
import com.mocentre.tehui.backend.param.DiscoverQueryParam;
import com.mocentre.tehui.common.service.BaseBackendService;

/**
 * 类DiscoverService.java的实现描述：发现页service
 * 
 * @author yukaiji 2016年11月14日
 */
@Component
public class DiscoverService extends BaseBackendService {

    @Autowired
    private DiscoverManageService discoverManageService;
    @Autowired
    private CategoryManageService categoryManageService;

    public List<CategoryInstance> queryCategory() {
        ListResult<CategoryInstance> result = categoryManageService.getAllCategoryList(generateRequestId());
        if (result.isSuccess()) {
            return result.getData();
        } else {
            return null;
        }
    }

    /**
     * 查询所有的配置页
     * 
     * @return
     */
    public ListJsonResult<DiscoverInstance> queryDiscoverPage(DiscoverQueryParam discoverParam) {
        ListJsonResult<DiscoverInstance> result = discoverManageService.queryPage(discoverParam);
        return result;
    }

    /**
     * 添加
     * 
     * @param discover
     * @return
     */
    public BaseResult discoverAdd(DiscoverParam discover) {
        return discoverManageService.addDiscover(discover);
    }

    /**
     * 修改
     * 
     * @param discover
     * @return
     */
    public BaseResult discoverEdit(DiscoverParam discover) {
        return discoverManageService.updateDiscover(discover);
    }

    public BaseResult show(Long id, Integer show) {
        BaseResult result = discoverManageService.show(id, show, generateRequestId());
        return result;
    }

    public BaseResult delete(String idList) {
        String[] sIdList = idList.split(",");
        List<Long> lIdList = new ArrayList<Long>();
        for (String id : sIdList) {
            lIdList.add(Long.valueOf(id));
        }
        BaseResult result = discoverManageService.deleteDiscover(lIdList, generateRequestId());
        return result;
    }

    public PlainResult<DiscoverAllMsgInstance> prviewEdit(Long id) {
        PlainResult<DiscoverAllMsgInstance> pr = new PlainResult<DiscoverAllMsgInstance>();
        PlainResult<DiscoverAllMsgInstance> result = discoverManageService.preEdit(id, generateRequestId());
        if (result.isSuccess()) {
            pr = result;
        } else {
            pr.setErrorMessage("1001", "数据不存在");
        }
        return pr;
    }

}
