package com.mocentre.tehui.td.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.MemberAccountManageService;
import com.mocentre.tehui.backend.model.MemberAccountInstance;
import com.mocentre.tehui.backend.model.MemberAccountKeyInstance;
import com.mocentre.tehui.backend.param.MemberAccountParam;
import com.mocentre.tehui.backend.param.MemberAccountQueryParam;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.core.cache.RedisCache;
import com.mocentre.tehui.core.controller.BaseController;

/**
 * Created by 王雪莹 on 2017/6/21.
 */
@Controller
@RequestMapping("/td/memberAccount")
public class MemberAccountController extends BaseController {

    @Autowired
    private MemberAccountManageService memberAccountManageService;
    @Autowired
    private RedisCache                 redisCache;

    @RequestMapping(value = "index.htm", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response) {
        return getNameSpace() + "index";
    }

    /**
     * 获取所有分页
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "getPage.htm", method = RequestMethod.POST)
    public void getPage(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<MemberAccountInstance> result = new ListJsonResult<MemberAccountInstance>();
        try {
            MemberAccountQueryParam params = super.bindDTParamClass(request, MemberAccountQueryParam.class);
            result = memberAccountManageService.getPage(params);
        } catch (Exception e) {
            LOGGER.error("getPage", e);
        }
        super.printJson(response, result.toJsonString());
    }

    /**
     * 新增合作方
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "addMemberAccount.htm", method = RequestMethod.POST)
    public void addAttribute(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<MemberAccountKeyInstance> pr = new PlainResult<MemberAccountKeyInstance>();
        try {
            MemberAccountParam attrParam = super.bindClass(request, MemberAccountParam.class);
            pr = memberAccountManageService.save(attrParam);
            //存入缓存
            if (pr.isSuccess()) {
                MemberAccountKeyInstance kvs = pr.getData();
                if (kvs != null) {
                    String keymark = kvs.getKeymark();
                    String appKey = kvs.getAppKey();
                    String appSercet = kvs.getAppSecret();
                    redisCache.saveMebAccount(keymark, kvs);
                    redisCache.saveMebAccountAppSecret(appKey, appSercet);
                }
            }
        } catch (Exception e) {
            pr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 跳转属性添加页面
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.GET)
    public String add(HttpServletRequest request, HttpServletResponse response, Model model) {
        MemberAccountInstance instance = new MemberAccountInstance();
        instance.setAppKey(DigestUtils.md5Hex(CommUtil.generateUUID()));
        instance.setAppSecret(DigestUtils.md5Hex(CommUtil.generateUUID()));
        model.addAttribute("memberAccount", instance);
        return getNameSpace() + "add";
    }

    /**
     * 跳转属性修改页面
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "edit.htm", method = RequestMethod.GET)
    public String edit(Long id, HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            PlainResult<MemberAccountInstance> pr = memberAccountManageService.getById(id, generageRequestId());
            if (pr.isSuccess()) {
                model.addAttribute("memberAccount", pr.getData());
            } else {
                model.addAttribute("errorMsg", pr.getMessage());
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
     * 修改一个属性
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "editMemberAccount.htm", method = RequestMethod.POST)
    public void editAttribute(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<MemberAccountKeyInstance> pr = new PlainResult<MemberAccountKeyInstance>();
        try {
            MemberAccountParam maParam = super.bindClass(request, MemberAccountParam.class);
            if (maParam.getId() == null) {
                pr.setErrorMessage("1001", "id为空");
            }
            if (pr.isSuccess()) {
                pr = memberAccountManageService.update(maParam);
                if (pr.isSuccess()) {
                    MemberAccountKeyInstance kvs = pr.getData();
                    if (kvs != null) {
                        String oldAppKey = kvs.getOldAppkey();
                        String oldKeymark = kvs.getOldKeymark();
                        String keymark = kvs.getKeymark();
                        String appKey = kvs.getAppKey();
                        String appSercet = kvs.getAppSecret();
                        if (!oldAppKey.equals(appKey)) {
                            redisCache.delMebAccountAppSecret(oldAppKey);
                        }
                        if (!oldKeymark.equals(keymark)) {
                            redisCache.delMebAccount(oldKeymark);
                        }
                        redisCache.saveMebAccount(keymark, kvs);
                        redisCache.saveMebAccountAppSecret(appKey, appSercet);
                    }
                }
            }
        } catch (Exception e) {
            pr.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, pr.toJsonString());
    }

    /**
     * 删除一个属性
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "delById.htm", method = RequestMethod.POST)
    public void delById(Long id, HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        try {
            br = memberAccountManageService.delById(id, generageRequestId());
        } catch (Exception e) {
            LOGGER.error("delById", e);
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }

    /**
     * 更新启用状态
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "updateIsDeny.htm", method = RequestMethod.POST)
    public void updateIsDeny(HttpServletRequest request, HttpServletResponse response) {
        BaseResult br = new BaseResult();
        String id = request.getParameter("id");
        String isDeny = request.getParameter("isDeny");
        try {
            br = memberAccountManageService.updateIsDeny(Long.parseLong(id), isDeny, generageRequestId());
        } catch (Exception e) {
            LOGGER.error("updateIsDeny", e);
            br.setErrorMessage("999", "系统异常");
        }
        super.printJson(response, br.toJsonString());
    }

}
