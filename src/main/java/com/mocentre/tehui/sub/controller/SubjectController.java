package com.mocentre.tehui.sub.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.ListResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.backend.model.SubjectAllMsgInstance;
import com.mocentre.tehui.backend.model.SubjectAndTmpInstance;
import com.mocentre.tehui.backend.model.SubjectGoodsInstance;
import com.mocentre.tehui.backend.model.SubjectInstance;
import com.mocentre.tehui.backend.model.SubjectTempletInstance;
import com.mocentre.tehui.backend.param.SubjectGoodsParam;
import com.mocentre.tehui.backend.param.SubjectGoodsQueryParam;
import com.mocentre.tehui.backend.param.SubjectParam;
import com.mocentre.tehui.backend.param.SubjectQueryParam;
import com.mocentre.tehui.common.constant.SessionKeyConstant;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.sub.service.SubjectService;

/**
 * 专题controller Created by yukaiji on 2016/12/03.
 */
@Controller
@RequestMapping("/sub/subject")
public class SubjectController extends BaseController {

    @Autowired
    private SubjectService subjectService;

    /**
     * 跳转到首页
     */
    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response) {
        return getNameSpace() + "index";
    }

    /**
     * 跳转到添加页面
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.GET)
    public String add(Model model, HttpServletRequest request, HttpServletResponse response) {
        try {
            ListResult<SubjectTempletInstance> lr = subjectService.querySubTmp();
            if (lr.isSuccess()) {
                model.addAttribute("templetList", lr.getData());
            } else {
                model.addAttribute("errorMsg", "访问失败");
                return getErrorIndex();
            }
        } catch (Exception e) {
            LOGGER.error("add", e);
            model.addAttribute("errorMsg", "系统异常");
            return getErrorIndex();
        }
        return getNameSpace() + "add";
    }

    /**
     * 跳转到修改页面
     */
    @RequestMapping(value = "edit.htm", method = RequestMethod.GET)
    public String edit(Long id, Model model, HttpServletRequest request, HttpServletResponse response) {
        try {
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            PlainResult<SubjectAndTmpInstance> pr = subjectService.subEdit(id, Long.parseLong(shopId.toString()));
            if (pr.isSuccess()) {
                SubjectAndTmpInstance result = pr.getData();
                model.addAttribute("templetList", result.getSubTempList());
                model.addAttribute("subject", result.getSubject());
            } else {
                model.addAttribute("errorMsg", "访问失败");
                return getErrorIndex();
            }
        } catch (Exception e) {
            LOGGER.error("edit", e);
            model.addAttribute("errorMsg", "系统异常");
            return getErrorIndex();
        }
        return getNameSpace() + "edit";
    }

    /**
     * 跳转到添加关联商品页面
     */
    @RequestMapping(value = "addGoods.htm", method = RequestMethod.GET)
    public String addGoods(HttpServletRequest request, HttpServletResponse response, Model model) {
        String subId = request.getParameter("subId");
        try {
            PlainResult<SubjectAllMsgInstance> pr = subjectService.subGoodsAdd(Long.parseLong(subId),
                    generageRequestId());
            if (pr.isSuccess()) {
                SubjectAllMsgInstance subAll = pr.getData();
                model.addAttribute("cyList", subAll.getCategoryList());
                model.addAttribute("subject", subAll.getSubject());
            } else {
                model.addAttribute("errorMsg", "访问失败");
                return getErrorIndex();
            }
        } catch (Exception e) {
            LOGGER.error("edit", e);
            model.addAttribute("errorMsg", "系统异常");
            return getErrorIndex();
        }
        return getNameSpace() + "addgoods";
    }

    /**
     * 跳转到修改关联商品页面
     */
    @RequestMapping(value = "editGoods.htm", method = RequestMethod.GET)
    public String editGoods(HttpServletRequest request, HttpServletResponse response, Model model) {
        String subGoodsId = request.getParameter("id");
        String subId = request.getParameter("subId");
        try {
            PlainResult<SubjectAllMsgInstance> pr = subjectService.subGoodsEdit(Long.parseLong(subId),
                    Long.parseLong(subGoodsId), generageRequestId());
            if (pr.isSuccess()) {
                SubjectAllMsgInstance subAll = pr.getData();
                model.addAttribute("subjectGoods", subAll.getSubjectGoods());
                model.addAttribute("subject", subAll.getSubject());
            } else {
                model.addAttribute("errorMsg", "访问失败");
                return getErrorIndex();
            }
        } catch (Exception e) {
            LOGGER.error("edit", e);
            model.addAttribute("errorMsg", "系统异常");
            return getErrorIndex();
        }
        return getNameSpace() + "editgoods";
    }

    /**
     * 跳转到专题与商品关联详细页
     */
    @RequestMapping(value = "view.htm", method = RequestMethod.GET)
    public String view(Long id, Model model) {
        PlainResult<SubjectInstance> pr = subjectService.querySubjectById(id, generageRequestId());
        if (pr.isSuccess()) {
            model.addAttribute("id", id);
            model.addAttribute("subject", pr.getData());
            return getNameSpace() + "view";
        } else {
            model.addAttribute("errorMsg", pr.getMessage());
            return getErrorIndex();
        }
    }

    /**
     * 分页查询
     */
    @RequestMapping(value = "query.htm", method = RequestMethod.POST)
    public void query(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<SubjectInstance> br = new ListJsonResult<SubjectInstance>();
        Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
        try {
            SubjectQueryParam subjectQueryParam = bindClass(request, SubjectQueryParam.class);
            br = subjectService.querySubjectPage(subjectQueryParam, shopId);
        } catch (Exception e) {
            LOGGER.error("query", e);
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 分页查询专题所属商品信息
     */
    @RequestMapping(value = "querySubjectGoods.htm", method = RequestMethod.POST)
    public void querySubjectGoods(Long id, HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<SubjectGoodsInstance> br = new ListJsonResult<>();
        try {
            SubjectGoodsQueryParam sbjGoodsQueryParam = bindClass(request, SubjectGoodsQueryParam.class);
            sbjGoodsQueryParam.setSubjectId(id);
            br = subjectService.querySubjectGoodsPage(sbjGoodsQueryParam);
        } catch (Exception e) {
            LOGGER.error("querySubjectGoods", e);
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 添加专题
     */
    @RequestMapping(value = "addSubject.htm", method = RequestMethod.POST)
    public void addSubject(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            SubjectParam subParam = super.bindClass(request, SubjectParam.class);
            subParam.setRequestId(generageRequestId());
            if (shopId != null) {
                subParam.setShopId((Long) shopId);
                br = subjectService.addSubject(subParam);
            } else {
                br.setErrorMessage("1001", "添加失败");
            }
        } catch (Exception e) {
            LOGGER.error("addSubject", e);
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 修改专题
     */
    @RequestMapping(value = "editSubject.htm", method = RequestMethod.POST)
    public void editSubject(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            Object shopId = request.getSession().getAttribute(SessionKeyConstant.SHOP);
            SubjectParam subParam = super.bindClass(request, SubjectParam.class);
            subParam.setRequestId(generageRequestId());
            if (shopId != null) {
                subParam.setShopId(Long.valueOf(shopId.toString()));
                br = subjectService.editSubject(subParam);
            } else {
                br.setErrorMessage("1001", "修改失败");
            }
        } catch (Exception e) {
            LOGGER.error("editSubject", e);
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 删除一个专题
     */
    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        try {
            br = subjectService.delete(id);
        } catch (Exception e) {
            LOGGER.error("delete", e);
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 添加专题关联商品
     */
    @RequestMapping(value = "addSubjectGoods.htm", method = RequestMethod.POST)
    public void addSubjetGoods(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            SubjectGoodsParam subGoodsParam = super.bindClass(request, SubjectGoodsParam.class);
            boolean paramValid = subGoodsParam.getGoodsId() == null || StringUtils.isBlank(subGoodsParam.getGoodsImg())
                    || StringUtils.isBlank(subGoodsParam.getTagline());
            if (paramValid) {
                br.setErrorMessage("1000", "参数错误");
                super.printJson(response, br.toJsonString());
                return;
            }
            br = subjectService.addSubjectGoods(subGoodsParam);
        } catch (Exception e) {
            LOGGER.error("addSubjectGoods", e);
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 修改专题关联商品
     */
    @RequestMapping(value = "editSubjectGoods.htm", method = RequestMethod.POST)
    public void editSubjetGoods(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            SubjectGoodsParam subGoodsParam = super.bindClass(request, SubjectGoodsParam.class);
            br = subjectService.editSubjectGoods(subGoodsParam);
        } catch (Exception e) {
            LOGGER.error("editSubjectGoods", e);
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 删除一个专题关联商品
     */
    @RequestMapping(value = "deleteSubjectGoods.htm", method = RequestMethod.POST)
    public void deleteSubjectGoods(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        try {
            br = subjectService.deleteSubGoods(id);
            if (!br.isSuccess()) {
                br.setErrorMessage("1001", "删除失败");
            }
        } catch (Exception e) {
            LOGGER.error("deleteSubjectGoods", e);
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 是否展示
     */
    @RequestMapping(value = "updateshow.htm", method = RequestMethod.POST)
    public void show(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        String show = request.getParameter("show");
        try {
            br = subjectService.show(Long.parseLong(id), show, generageRequestId());
        } catch (Exception e) {
            LOGGER.error("show", e);
            br.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, br.toJsonString());
    }

}
