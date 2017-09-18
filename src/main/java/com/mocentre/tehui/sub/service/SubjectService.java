/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.sub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.CategoryManageService;
import com.mocentre.tehui.SubjectManageService;
import com.mocentre.tehui.backend.model.SubjectAllMsgInstance;
import com.mocentre.tehui.backend.model.SubjectAndTmpInstance;
import com.mocentre.tehui.backend.model.SubjectGoodsInstance;
import com.mocentre.tehui.backend.model.SubjectInstance;
import com.mocentre.tehui.backend.model.SubjectTempletInstance;
import com.mocentre.tehui.backend.param.SubjectGoodsParam;
import com.mocentre.tehui.backend.param.SubjectGoodsQueryParam;
import com.mocentre.tehui.backend.param.SubjectParam;
import com.mocentre.tehui.backend.param.SubjectQueryParam;

/**
 * 类SubjectService.java的实现描述：专题service
 * 
 * @author yukaiji 2016年12月03日
 */
@Component
public class SubjectService {

    @Autowired
    private SubjectManageService  subjectManageService;
    @Autowired
    private CategoryManageService categoryManageService;

    public PlainResult<SubjectInstance> querySubjectById(Long subjectId, String requestId) {
        PlainResult<SubjectInstance> pr = subjectManageService.getSubjectById(subjectId, requestId);
        return pr;
    }

    /**
     * 查询所有的专题
     */
    public ListJsonResult<SubjectInstance> querySubjectPage(SubjectQueryParam subjectQuery, Object shopId) {
        ListJsonResult<SubjectInstance> br = new ListJsonResult<SubjectInstance>();
        ListJsonResult<SubjectInstance> result = subjectManageService.querySubjectPage(subjectQuery);
        if (result != null) {
            if (result.isSuccess()) {
                br = result;
            }
        }
        return br;
    }

    /**
     * 查询所有的专题关联商品
     */
    public ListJsonResult<SubjectGoodsInstance> querySubjectGoodsPage(SubjectGoodsQueryParam sbjGoodsQueryParam) {
        ListJsonResult<SubjectGoodsInstance> br = new ListJsonResult<SubjectGoodsInstance>();
        ListJsonResult<SubjectGoodsInstance> result = subjectManageService.querySubjectGoodsPage(sbjGoodsQueryParam);
        if (result != null) {
            if (result.isSuccess()) {
                br = result;
            }
        }
        return br;
    }

    /**
     * 查询所有模板
     */
    public ListResult<SubjectTempletInstance> querySubTmp() {
        ListResult<SubjectTempletInstance> lr = new ListResult<SubjectTempletInstance>();
        ListResult<SubjectTempletInstance> result = subjectManageService.getSubTmp();
        if (result != null) {
            if (result.isSuccess()) {
                lr = result;
            } else {
                lr.setErrorMessage("999", "系统错误");
            }
        }
        return lr;
    }

    /**
     * 获取专题和所有模板
     */
    public PlainResult<SubjectAndTmpInstance> subEdit(Long id, Long shopId) {
        PlainResult<SubjectAndTmpInstance> pr = new PlainResult<SubjectAndTmpInstance>();
        PlainResult<SubjectAndTmpInstance> result = subjectManageService.subEdit(id, shopId);
        if (result != null) {
            if (result.isSuccess()) {
                pr = result;
            } else {
                pr.setErrorMessage("999", "系统错误");
            }
        }
        return pr;
    }

    public PlainResult<SubjectAllMsgInstance> subGoodsAdd(Long subjectId, String requestId) {
        PlainResult<SubjectAllMsgInstance> pr = subjectManageService.subGoodsAdd(subjectId, requestId);
        return pr;
    }

    /**
     * 获取专题关联商品
     */
    public PlainResult<SubjectAllMsgInstance> subGoodsEdit(Long subjectId, Long subGoodsId, String requestId) {
        PlainResult<SubjectAllMsgInstance> pr = subjectManageService.subGoodsEdit(subjectId, subGoodsId, requestId);
        return pr;
    }

    /**
     * 添加一个专题
     */
    public BaseResult addSubject(SubjectParam subParam) {
        BaseResult br = new BaseResult();
        BaseResult result = subjectManageService.addSubject(subParam);
        if (result != null) {
            if (!result.isSuccess()) {
                br.setErrorMessage("1001", "添加失败");
            } else {
                br = result;
            }
        } else {
            br.setErrorMessage("1001", "添加失败");
        }
        return br;
    }

    /**
     * 修改一个专题
     */
    public BaseResult editSubject(SubjectParam subParam) {
        BaseResult br = new BaseResult();
        BaseResult result = subjectManageService.updateSubject(subParam);
        if (result != null) {
            if (!result.isSuccess()) {
                br.setErrorMessage("1001", "修改失败");
            }
        } else {
            br.setErrorMessage("1001", "修改失败");
        }
        return br;
    }

    /**
     * 删除一个专题
     */
    public BaseResult delete(String id) {
        BaseResult br = new BaseResult();
        BaseResult result = subjectManageService.deleteSubject(Long.valueOf(id));
        if (result != null) {
            if (!result.isSuccess()) {
                br.setErrorMessage(result.getCode(), result.getMessage());
            }
        } else {
            br.setErrorMessage("1001", "删除失败");
        }
        return br;
    }

    /**
     * 添加一个专题关联商品
     */
    public BaseResult addSubjectGoods(SubjectGoodsParam subGoodsParam) {
        BaseResult br = new BaseResult();
        BaseResult result = subjectManageService.addSubjectGoods(subGoodsParam);
        if (result != null) {
            if (!result.isSuccess()) {
                br.setErrorMessage("1001", "添加失败");
            } else {
                br = result;
            }
        } else {
            br.setErrorMessage("1001", "添加失败");
        }
        return br;
    }

    /**
     * 修改一个专题关联商品
     */
    public BaseResult editSubjectGoods(SubjectGoodsParam subGoodsParam) {
        BaseResult br = new BaseResult();
        BaseResult result = subjectManageService.updateSubjectGoods(subGoodsParam);
        if (result != null) {
            if (!result.isSuccess()) {
                br.setErrorMessage("1001", "修改失败");
            } else {
                br = result;
            }
        } else {
            br.setErrorMessage("1001", "修改失败");
        }
        return br;
    }

    /**
     * 删除一个专题关联商品
     */
    public BaseResult deleteSubGoods(String id) {
        BaseResult br = new BaseResult();
        BaseResult result = subjectManageService.deleteSubjectGoods(Long.valueOf(id));
        if (result != null) {
            if (!result.isSuccess()) {
                br.setErrorMessage("1001", "删除失败");
            }
        } else {
            br.setErrorMessage("1001", "删除失败");
        }
        return br;
    }

    /**
     * 更改是否展示
     */
    public BaseResult show(Long id, String show, String requestId) {
        BaseResult br = new BaseResult();
        BaseResult result = subjectManageService.show(id, show, requestId);
        if (result != null) {
            if (!result.isSuccess()) {
                br.setErrorMessage("1001", "修改失败");
            }
        } else {
            br.setErrorMessage("1001", "修改失败");
        }
        return br;
    }

}
