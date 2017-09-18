/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.RuleManageService;
import com.mocentre.tehui.backend.model.RuleInstance;
import com.mocentre.tehui.backend.param.RuleParam;
import com.mocentre.tehui.common.util.CommUtil;

/**
 * 类RuleService.java的实现描述：菜单service
 * 
 * @author sz.gong 2016年11月8日 下午4:10:14
 */
@Component
public class RuleService {

    @Autowired
    private RuleManageService ruleMagService;

    public ListResult<RuleInstance> queryList(String title) {
        ListResult<RuleInstance> br = new ListResult<RuleInstance>();
        String requestId = CommUtil.generateUUID();
        ListResult<RuleInstance> result = ruleMagService.queryList(title, requestId);
        if (result != null) {
            if (result.isSuccess()) {
                br.setData(result.getData());
            }
        } else {
            br.setErrorMessage("99", "查询失败");
        }
        return br;
    }

    public List<RuleInstance> queryRuleCascade() {
        List<RuleInstance> list = null;
        String requestId = CommUtil.generateUUID();
        ListResult<RuleInstance> result = ruleMagService.queryCascade(requestId);
        if (result != null) {
            if (result.isSuccess()) {
                list = result.getData();
            }
        }
        return list;
    }

    public RuleInstance queryRuleById(Long id) {
        RuleInstance ruleIns = null;
        String requestId = CommUtil.generateUUID();
        PlainResult<RuleInstance> result = ruleMagService.queryById(id, requestId);
        if (result != null) {
            if (result.isSuccess()) {
                ruleIns = result.getData();
            }
        }
        return ruleIns;
    }

    public Boolean saveRule(RuleParam rule) {
        rule.setRequestId(CommUtil.generateUUID());
        BaseResult result = ruleMagService.save(rule);
        if (result != null) {
            if (!result.isSuccess()) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public Boolean deleteRule(List<Long> idList) {
        String requestId = CommUtil.generateUUID();
        BaseResult result = ruleMagService.delete(idList, requestId);
        if (result != null) {
            if (!result.isSuccess()) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

}
