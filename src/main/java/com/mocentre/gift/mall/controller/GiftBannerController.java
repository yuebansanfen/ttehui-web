package com.mocentre.gift.mall.controller;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.gift.GiftBannerManageService;
import com.mocentre.gift.backend.model.GiftBannerInstance;
import com.mocentre.gift.backend.param.GiftBannerParam;
import com.mocentre.gift.backend.param.GiftBannerQueryParam;
import com.mocentre.tehui.core.annotation.SysLog;
import com.mocentre.tehui.core.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 礼品平台 banner图controller
 * @author liqifan
 * @date 创建时间：2017年4月7日 下午3:45:55
 */
@Controller
@RequestMapping("/gmall/banner")
public class GiftBannerController extends BaseController{
	
	@Autowired
    private GiftBannerManageService giftBannerManageService;

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
     * 添加页面
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "add.htm", method = RequestMethod.GET)
    public String add(HttpServletRequest request, HttpServletResponse response) {
        return getNameSpace() + "add";
    }

    /**
     * 修改基本信息页面
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "edit.htm", method = RequestMethod.GET)
    public String editBase(Long id, Model model) {
        try {
        	GiftBannerParam giftBannerParam = giftBannerManageService.getGiftBanner(id);
            model.addAttribute("giftBanner", giftBannerParam);
        } catch (Exception e) {
            LOGGER.error("edit", e);
        }
        return getNameSpace() + "edit";
    }
    
    /**
     * 查询banner
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "query.htm", method = RequestMethod.POST)
    public void queryPaged(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<GiftBannerInstance> lr = new ListJsonResult<GiftBannerInstance>();
        try {
            GiftBannerQueryParam giftBannerQueryParam = bindClass(request, GiftBannerQueryParam.class);
            lr = giftBannerManageService.queryPage(giftBannerQueryParam);
        } catch (Exception e) {
            LOGGER.error("queryPaged", e);
        }
        super.printJson(response, lr.toJsonString());
    }
    
    /**
     * 保存banner
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "save.htm", method = RequestMethod.POST)
	public void save(HttpServletRequest request, HttpServletResponse response) {
    	BaseResult br = new BaseResult();
		try {
			GiftBannerParam giftBanner = this.bindClass(request, GiftBannerParam.class);
			Long id = giftBanner.getId();
			if (id != null) {
				giftBannerManageService.updateGiftBanner(giftBanner);
			} else {
				giftBannerManageService.saveGiftBanner(giftBanner);
				br.setSuccess(true);
			}
		} catch (Exception e) {
			LOGGER.error("save", e);
		}
		super.printJson(response, br.toJsonString());
	}
    
    /**
     * 删除banner
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
	@SysLog(title = "删除banner", value = "/gps/banner/delete.htm", description = "删除banner")
	public void delete(HttpServletRequest request, HttpServletResponse response) {
    	 BaseResult br = new BaseResult();
         try {
             String ids = request.getParameter("ids");
             List<Long> idList = new ArrayList<Long>();
             for (String id : ids.split(",")) {
                 idList.add(Long.parseLong(id));
             }
             br = giftBannerManageService.deleteById(idList,generageRequestId());
         } catch (Exception e) {
             LOGGER.error("delete", e);
             br.setErrorMessage("99", "删除失败");
         }
         printJson(response, br.toJsonString());
	}
}
