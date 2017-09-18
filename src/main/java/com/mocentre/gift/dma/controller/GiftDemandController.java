package com.mocentre.gift.dma.controller;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.gift.GiftDemandManageService;
import com.mocentre.gift.backend.model.GiftDemandInstance;
import com.mocentre.gift.backend.param.GiftDemandQueryParam;
import com.mocentre.tehui.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 礼品平台 极速获取方案controller
 * @author liqifan
 * @date 创建时间：2017年4月7日 下午2:01:09
 */
@Controller
@RequestMapping("/gdma/demand")
public class GiftDemandController extends BaseController{

	@Autowired
    private GiftDemandManageService giftGiftDemandManageService;
	/**
     * 跳转首页
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
     * 查询方案
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "query.htm", method = RequestMethod.POST)
    public void queryPaged(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<GiftDemandInstance> result = new ListJsonResult<GiftDemandInstance>();
        try {
            GiftDemandQueryParam giftDemandQueryParam = bindClass(request, GiftDemandQueryParam.class);
            result = giftGiftDemandManageService.queryPage(giftDemandQueryParam);
        } catch (Exception e) {
            LOGGER.error("queryPaged", e);
        }
        super.printJson(response, result.toJsonString());
    }
    
    /**
     * 切换状态
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "changeStatus.htm", method = RequestMethod.GET)
    public String changeStatus(HttpServletRequest request, HttpServletResponse response) {
        try {
        	Long id = Long.valueOf(request.getParameter("id"));
        	Map<String,Object> paramMap = new HashMap<String,Object>();
        	paramMap.put("id", id);
        	giftGiftDemandManageService.changeGiftDemandStatus(paramMap);
        } catch (Exception e) {
            LOGGER.error("changeStatus", e);
        }
        return getNameSpace() + "index";
    }
    
    /**
     * 删除方案
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
	public void delete(HttpServletRequest request, HttpServletResponse response) {
    	 BaseResult br = new BaseResult();
         try {
             String ids = request.getParameter("ids");
             List<Long> idList = new ArrayList<Long>();
             for (String id : ids.split(",")) {
                 idList.add(Long.parseLong(id));
             }
             br = giftGiftDemandManageService.deleteById(idList,generageRequestId());
         } catch (Exception e) {
             LOGGER.error("delete", e);
             br.setErrorMessage("99", "删除失败");
         }
         printJson(response, br.toJsonString());
	}
}
