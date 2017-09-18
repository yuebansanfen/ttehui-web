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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.RoleManageService;
import com.mocentre.tehui.backend.model.RoleInstance;
import com.mocentre.tehui.backend.model.RuleInstance;
import com.mocentre.tehui.backend.param.RoleParam;
import com.mocentre.tehui.backend.param.RoleQueryParam;
import com.mocentre.tehui.common.util.CommUtil;

/**
 * 类RoleService.java的实现描述：角色service
 * 
 * @author sz.gong 2016年11月8日 下午6:07:05
 */
@Component
public class RoleService {

    @Autowired
    private RoleManageService roleMagService;

    public List<RoleInstance> queryRole() {
        String requestId = CommUtil.generateUUID();
        List<RoleInstance> roleList = new ArrayList<RoleInstance>();
        ListResult<RoleInstance> result = roleMagService.queryEnableRole(requestId);
        if (result != null) {
            if (result.isSuccess()) {
                roleList = result.getData();
            }
        }
        return roleList;
    }

    public ListJsonResult<RoleInstance> queryRolePage(RoleQueryParam roleParamParam) {
        ListJsonResult<RoleInstance> result = new ListJsonResult<RoleInstance>();
        roleParamParam.setRequestId(CommUtil.generateUUID());
        result = roleMagService.queryPage(roleParamParam);
        return result;
    }

    public List<RuleInstance> preAddRole() {
        List<RuleInstance> ruleList = null;
        String requestId = CommUtil.generateUUID();
        ListResult<RuleInstance> result = roleMagService.preAdd(requestId);
        if (result != null) {
            if (result.isSuccess()) {
                ruleList = result.getData();
            }
        }
        return ruleList;
    }

    public PlainResult<RoleInstance> preEditRole(Long id) {
        PlainResult<RoleInstance> result = new PlainResult<RoleInstance>();
        String requestId = CommUtil.generateUUID();
        result = roleMagService.preEdit(id, requestId);
        return result;
    }

    public BaseResult saveRole(RoleParam roleParam, String ruleIds) {
        BaseResult br = new BaseResult();
        List<Long> ruleIdList = new ArrayList<Long>();
        if (StringUtils.isNotBlank(ruleIds)) {
            String[] ruleid = ruleIds.split(",");
            for (int i = 0; i < ruleid.length; i++) {
                String rid = ruleid[i];
                ruleIdList.add(Long.parseLong(rid));
            }
        }
        roleParam.setRuleIds(ruleIdList);
        BaseResult result = roleMagService.save(roleParam);
        if (result == null) {
            br.setErrorMessage("99", "接口异常");
            return br;
        }
        if (!result.isSuccess()) {
            String code = result.getCode();
            if ("1001".equals(code)) {
                br.setErrorMessage("101", "角色已存在");
            } else {
                br.setErrorMessage("100", "新增失败");
            }
        }
        return br;
    }

    public BaseResult assignRule(Long id, String ruleIds) {
        BaseResult br = new BaseResult();
        List<Long> ruleIdList = new ArrayList<Long>();
        if (StringUtils.isNotBlank(ruleIds)) {
            String[] ruleid = ruleIds.split(",");
            for (int i = 0; i < ruleid.length; i++) {
                String rid = ruleid[i];
                ruleIdList.add(Long.parseLong(rid));
            }
        }
        RoleParam roleParam = new RoleParam();
        roleParam.setId(id);
        roleParam.setRuleIds(ruleIdList);
        BaseResult result = roleMagService.assignRule(roleParam);
        if (result == null) {
            br.setErrorMessage("99", "接口异常");
            return br;
        }
        br.setCode(result.getCode());
        br.setMessage(result.getMessage());
        return br;
    }

    public BaseResult deleteRole(List<Long> idList) {
        BaseResult br = new BaseResult();
        String requestId = CommUtil.generateUUID();
        BaseResult result = roleMagService.delete(idList, requestId);
        if (result == null) {
            br.setErrorMessage("99", "接口异常");
            return br;
        }
        if (!result.isSuccess()) {
            br.setErrorMessage("100", "删除失败");
        }
        return br;
    }

}
