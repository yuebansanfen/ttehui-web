package com.mocentre.tehui.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;

import com.mocentre.tehui.backend.model.RuleInstance;
import com.mocentre.tehui.common.constant.SessionKeyConstant;

public class CentreServlet extends DispatcherServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = request.getRequestURI();
        if ("/".equals(url) || url.indexOf("main") > 0 || url.indexOf("front") > 0) {
            super.render(mv, request, response);
            return;
        }
        String base = request.getContextPath();
        if (url != null) {
            if (!"/".equals(base)) {
                url = url.replace(base, "");
            }
            if (url.startsWith("//", 0)) {
                url = url.replaceFirst("//", "");
            } else if (url.startsWith("/", 0)) {
                url = url.replaceFirst("/", "");
            }
            int index = url.indexOf(".htm");
            if (index > 0) {
                url = url.substring(0, index) + ".htm";
            }
        }
        List<RuleInstance> ruleList = (List<RuleInstance>) request.getSession().getAttribute(SessionKeyConstant.MENU);
        if (ruleList != null) {
            List<RuleInstance> menuList = new ArrayList<RuleInstance>();
            List<RuleInstance> pcList = new ArrayList<RuleInstance>();
            //排序好的父子链
            List<RuleInstance> pcs = this.getPClist(ruleList, url);
            for (int i = pcs.size() - 1; i >= 0; i--) {
                RuleInstance rule = pcs.get(i);
                pcList.add(rule);
            }
            if (pcList != null) {
                if (pcList.size() > 0)
                    mv.addObject("menus", pcList.get(0));
                if (pcList.size() > 1)
                    mv.addObject("cMenus", pcList.get(1));
                if (pcList.size() > 2)
                    mv.addObject("ccMenus", pcList.get(2));
                if (pcList.size() > 3)
                    mv.addObject("cccMenus", pcList.get(3));
            }
            if (pcs != null && pcs.size() >= 2) {
                RuleInstance rule = pcs.get(0);
                mv.addObject("curMenuid", rule.getId());
                if (rule.getType() == 2) {
                    RuleInstance r = pcs.get(1);
                    mv.addObject("showMenuid", r.getId());
                } else {
                    mv.addObject("showMenuid", rule.getId());
                }
            }
            //排序好的菜单树
            for (int i = 0; i < ruleList.size(); i++) {
                RuleInstance ruleIns = (RuleInstance) ruleList.get(i);
                if (ruleIns.getType().intValue() == 1) {
                    RuleInstance menu = new RuleInstance();
                    menu.setTitle(ruleIns.getTitle());
                    menu.setId(ruleIns.getId());
                    menu.setParentId(ruleIns.getParentId());
                    menu.setIcon(ruleIns.getIcon());
                    menu.setType(ruleIns.getType());
                    menu.setUrl(ruleIns.getUrl());
                    menu.setChildren(ruleIns.getChildren());
                    menuList.add(menu);
                }
            }
            mv.addObject("menuList", getMenuTree(menuList));
            mv.addObject("pcList", pcList);
        }
        mv.addObject("base", base);
        mv.addObject("baseDescribe", "天天特惠");
        super.render(mv, request, response);
    }

    /*
     * 父子关系列表
     */
    private List<RuleInstance> getPClist(List<RuleInstance> list, String url) {
        List<RuleInstance> pList = new ArrayList<RuleInstance>();
        RuleInstance rule = null;
        for (int i = 0; i < list.size(); i++) {
            RuleInstance r = list.get(i);
            if (r.getUrl().trim().equals(url.trim())) {
                rule = r;
                pList.add(r);
                break;
            }
        }
        if (rule != null) {
            buildPClist(pList, list, rule);
        }
        return pList;
    }

    private void buildPClist(List<RuleInstance> pList, List<RuleInstance> list, RuleInstance child) {
        RuleInstance rule = null;
        for (int i = 0; i < list.size(); i++) {
            RuleInstance ru = list.get(i);
            if (ru.getId().intValue() == child.getParentId().intValue()) {
                rule = ru;
                pList.add(ru);
            }
        }
        if (rule != null) {
            if (rule.getParentId() == 0) {
                return;
            } else {
                buildPClist(pList, list, rule);
            }
        }
    }

    /*
     * 菜单树
     */
    private List<RuleInstance> getMenuTree(List<RuleInstance> list) {

        List<RuleInstance> parents = new ArrayList<RuleInstance>();
        List<RuleInstance> others = new ArrayList<RuleInstance>();
        for (RuleInstance rule : list) {
            if (rule.getParentId() == 0) {
                rule.setChildren(new ArrayList<RuleInstance>());
                parents.add(rule);
            } else {
                others.add(rule);
            }
        }
        this.buildTree(parents, others);
        return parents;
    }

    private void buildTree(List<RuleInstance> parents, List<RuleInstance> others) {

        List<RuleInstance> record = new ArrayList<RuleInstance>();
        for (Iterator<RuleInstance> it = parents.iterator(); it.hasNext();) {
            RuleInstance vi = it.next();
            if (vi.getId() != null) {
                for (Iterator<RuleInstance> otherIt = others.iterator(); otherIt.hasNext();) {
                    RuleInstance inVi = otherIt.next();
                    if (vi.getId().longValue() == inVi.getParentId().longValue()) {
                        if (null == vi.getChildren()) {
                            vi.setChildren(new ArrayList<RuleInstance>());
                        }
                        vi.getChildren().add(inVi);
                        record.add(inVi);
                        otherIt.remove();
                    }
                }
            }
        }
        if (others.size() == 0) {
            return;
        } else {
            buildTree(record, others);
        }
    }

}
