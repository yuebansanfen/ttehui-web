package com.mocentre.tehui.goods.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.ActivityManageService;
import com.mocentre.tehui.NavigateMangeService;
import com.mocentre.tehui.SubjectManageService;
import com.mocentre.tehui.backend.model.ActivityInstance;
import com.mocentre.tehui.backend.model.NavigateInstance;
import com.mocentre.tehui.backend.model.SubjectInstance;
import com.mocentre.tehui.backend.param.NavigateParam;
import com.mocentre.tehui.backend.param.NavigateQueryParam;
import com.mocentre.tehui.core.controller.BaseController;

/**
 * 导航栏 Created by王雪莹 on 2016/12/20.
 */
@Controller
@RequestMapping("/goods/navigate")
public class NavigateController extends BaseController {

    @Autowired
    private NavigateMangeService  navigateMangeService;
    @Autowired
    private SubjectManageService  subjectMangeService;
    @Autowired
    private ActivityManageService actManageService;

    /**
     * 跳转初始化页面
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
     * 查询所有
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "getPage.htm", method = RequestMethod.POST)
    public void getPage(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<NavigateInstance> result = new ListJsonResult<NavigateInstance>();
        try {
            NavigateQueryParam queryParam = super.bindDTParamClass(request, NavigateQueryParam.class);
            result = navigateMangeService.getNavigatePage(queryParam);
        } catch (Exception e) {
            LOGGER.error("getPage", e);
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 新增
     * 
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.GET)
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) {
        return getNameSpace() + "add";
    }

    @RequestMapping(value = "edit.htm", method = RequestMethod.GET)
    public String edit(Long id, Model model) {
        try {
            PlainResult<NavigateInstance> pr = navigateMangeService.getNavigateById(id, generageRequestId());
            if (pr.isSuccess()) {
                NavigateInstance navIns = pr.getData();
                model.addAttribute("navigate", navIns);
            } else {
                model.addAttribute("errorMsg", pr.getMessage());
                return getErrorIndex();
            }
        } catch (Exception e) {
            LOGGER.error("edit", e);
        }
        return getNameSpace() + "edit";
    }

    @RequestMapping(value = "ajax/queryType.htm", method = RequestMethod.POST)
    public void queryType(HttpServletRequest request, HttpServletResponse response) {
        String type = request.getParameter("type");
        PlainResult<String> pr = new PlainResult<String>();
        try {
            JSONArray jArr = new JSONArray();
            if ("subject".equals(type)) {
                ListResult<SubjectInstance> lr = subjectMangeService.getShowSubjectList(generageRequestId());
                if (lr.isSuccess()) {
                    List<SubjectInstance> list = lr.getData();
                    for (SubjectInstance sub : list) {
                        JSONObject jObj = new JSONObject();
                        jObj.put("id", sub.getId());
                        jObj.put("name", sub.getTitle());
                        jArr.add(jObj);
                    }
                }
            } else if ("activity".equals(type)) {
                ListResult<ActivityInstance> lr = actManageService.getShowActivityList();
                if (lr.isSuccess()) {
                    List<ActivityInstance> actList = lr.getData();
                    for (ActivityInstance act : actList) {
                        JSONObject jObj = new JSONObject();
                        jObj.put("id", act.getId());
                        jObj.put("name", act.getTitle());
                        jArr.add(jObj);
                    }
                }
            }
            pr.setData(jArr.toJSONString());
        } catch (Exception e) {
            LOGGER.error("queryType", e);
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 保存
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "addNavigate.htm", method = RequestMethod.POST)
    public void addNavigate(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            NavigateParam navigateParam = super.bindClass(request, NavigateParam.class);
            navigateParam.setRequestId(generageRequestId());
            br = navigateMangeService.addNavigate(navigateParam);
        } catch (Exception e) {
            LOGGER.error("addNavigate", e);
            br.setErrorMessage("1001", "保存失败");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 删除
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String ids = request.getParameter("ids");
        try {
            String[] idArr = ids.split(",");
            if (idArr != null && idArr.length > 0) {
                List<Long> idList = new ArrayList<>();
                for (int i = 0; i < idArr.length; i++) {
                    idList.add(Long.parseLong(idArr[i]));
                }
                br = navigateMangeService.delByIdList(idList, generageRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("delete", e);
            br.setErrorMessage("1001", "删除失败");
        }
        super.printJson(response, br.toJsonString());
    }

}
