package com.mocentre.tehui.front.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.PlainResult;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.SubjectAllMsgFTInstance;
import com.mocentre.tehui.frontend.param.SubjectQueryParam;
import com.mocentre.tehui.frontend.service.SubjectManageService;

/**
 * 前台专题接口 Created by yukaiji on 2016/01/22.
 */

@Controller
@RequestMapping("/front/subject")
public class SubjectFTController extends BaseController {

    @Autowired
    private SubjectManageService subjectManageService;

    /**
     * 前台获取专题的信息以及所包含的商品信息.
     */
    @RequestMapping(value = "getSubjectWithGoodsList.htm", method = RequestMethod.POST)
    public void getSubjectWithGoodsList(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<SubjectAllMsgFTInstance> result = new PlainResult<>();
        SubjectQueryParam subParam = bindClass(request, SubjectQueryParam.class);
        subParam.setRequestId(generageRequestId());
        try {
            if (subParam.getSubjectId() == null) {
                result.setErrorMessage("1001", "专题id为空");
            }
            if (result.isSuccess()) {
                result = subjectManageService.getSubjectWithGoods(subParam);
            }
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

}
