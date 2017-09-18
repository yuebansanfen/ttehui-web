/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.sys.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.UserManageService;
import com.mocentre.tehui.backend.model.UserInstance;
import com.mocentre.tehui.backend.param.UserParam;
import com.mocentre.tehui.backend.param.UserQueryParam;
import com.mocentre.tehui.common.util.CommUtil;

/**
 * 类UserService.java的实现描述：用户service
 * 
 * @author sz.gong 2016年11月8日 下午3:53:49
 */
@Component
public class UserService {

    @Autowired
    private UserManageService userMagService;

    public ListJsonResult<UserInstance> queryPage(UserQueryParam userQueryParam) {
        ListJsonResult<UserInstance> br = new ListJsonResult<UserInstance>();
        userQueryParam.setRequestId(CommUtil.generateUUID());
        br = userMagService.queryPage(userQueryParam);
        return br;
    }

    public PlainResult<UserInstance> preEdit(Long id) {
        String requestId = CommUtil.generateUUID();
        PlainResult<UserInstance> prUser = userMagService.queryUserRole(id, requestId);
        return prUser;
    }

    public BaseResult updaterBase(UserParam user) {
        BaseResult result = userMagService.updateBase(user);
        return result;
    }

    public BaseResult saveOrUpdate(UserParam user, String roleids) {
        BaseResult br = new BaseResult();
        String[] roleArr = roleids.split(",");
        List<Long> roles = new ArrayList<Long>();
        for (int i = 0; i < roleArr.length; i++) {
            roles.add(Long.parseLong(roleArr[i]));
        }
        user.setRoles(roles);
        BaseResult result = userMagService.save(user);
        if (result != null && result.isSuccess()) {
            if (!result.isSuccess()) {
                String code = result.getCode();
                if ("1001".equals(code)) {
                    br.setErrorMessage("101", "用户已存在");
                } else {
                    br.setErrorMessage("100", "输入不能为空");
                }
            }
        } else {
            br.setErrorMessage("99", "系统繁忙，请稍后再试");
        }
        return br;
    }

    public BaseResult deleteById(List<Long> idList) {
        BaseResult br = new BaseResult();
        String requestId = CommUtil.generateUUID();
        BaseResult result = userMagService.delete(idList, requestId);
        if (result != null) {
            if (!result.isSuccess()) {
                br.setErrorMessage("100", "删除失败");
            }
        } else {
            br.setErrorMessage("99", "系统繁忙，请稍后再试");
        }
        return br;
    }

}
