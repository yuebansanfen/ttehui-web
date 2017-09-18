package com.mocentre.gift.ps.controller;

import com.mocentre.common.BaseResult;
import com.mocentre.common.ListJsonResult;
import com.mocentre.gift.GiftCustomerManageService;
import com.mocentre.gift.backend.model.GiftCustomerInstance;
import com.mocentre.gift.backend.param.GiftCustomerParam;
import com.mocentre.gift.backend.param.GiftCustomerQueryParam;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.core.annotation.SysLog;
import com.mocentre.tehui.core.controller.BaseController;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 礼品平台 客户controller
 * @author liqifan
 * @date 创建时间：2017年4月6日 上午11:36:15
 */
@Controller
@RequestMapping("/gps/customer")
public class GiftCustomerController extends BaseController{
	
	@Autowired
    private GiftCustomerManageService giftCustomerManageService;
	
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
    @RequestMapping(value = "edit_base.htm", method = RequestMethod.GET)
    public String editBase(Long id, Model model) {
        try {
            GiftCustomerInstance instance = giftCustomerManageService.getGiftCustomer(id);
            model.addAttribute("giftCustomer", instance);
        } catch (Exception e) {
            LOGGER.error("edit_base", e);
        }
        return getNameSpace() + "edit_base";
    }
    
    /**
     * 修改密码页面
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "edit_passwd.htm", method = RequestMethod.GET)
    public String editPassWord(Long id, Model model) {
        try {
            model.addAttribute("id", id);
        } catch (Exception e) {
            LOGGER.error("edit_passwd", e);
        }
        return getNameSpace() + "edit_passwd";
    }
    
    /**
     * 查询客户
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "query.htm", method = RequestMethod.POST)
    public void queryPaged(HttpServletRequest request, HttpServletResponse response) {
        ListJsonResult<GiftCustomerInstance> lr = new ListJsonResult<GiftCustomerInstance>();
        try {
            GiftCustomerQueryParam giftCustomerQueryParam = bindClass(request, GiftCustomerQueryParam.class);
            lr = giftCustomerManageService.queryPage(giftCustomerQueryParam);
        } catch (Exception e) {
            LOGGER.error("query", e);
        }
        super.printJson(response, lr.toJsonString());
    }
    
    /**
     * 保存客户
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "save.htm", method = RequestMethod.POST)
	@SysLog(title = "新增客户、编辑客户", value = "/gps/customer/save", description = "新增用户、编辑用户")
	public void save(HttpServletRequest request, HttpServletResponse response) {
    	BaseResult result = new BaseResult();
		try {
			GiftCustomerParam giftCustomer = this.bindClass(request, GiftCustomerParam.class);
			Long id = giftCustomer.getId();
			// 6位随机数
            String randomNum = "";
            for (int i = 0; i < 6; i++) {
                randomNum += (CommUtil.randomRang(0, 9));
            }
			if (id != null) {
				if (StringUtils.isNotBlank(giftCustomer.getPassword())) {
					giftCustomer.setPassword(DigestUtils.md5Hex(giftCustomer.getPassword() + randomNum));
					giftCustomer.setRandomNum(randomNum);
				}
				result = giftCustomerManageService.updateGiftCustomer(giftCustomer);
			} else {
				giftCustomer.setPassword(DigestUtils.md5Hex(giftCustomer.getPassword() + randomNum));
				giftCustomer.setRandomNum(randomNum);
				result = giftCustomerManageService.saveGiftCustomer(giftCustomer);
			}
		} catch (Exception e) {
			LOGGER.error("save", e);
		}
		super.printJson(response, result.toJsonString());
	}
    
    /**
     * 修改密码
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "save_passwd.htm", method = RequestMethod.POST)
	@SysLog(title = "修改密码", value = "/gps/customer/save_passwd", description = "修改密码")
	public void savePasswd(HttpServletRequest request, HttpServletResponse response) {
    	BaseResult result = new BaseResult();
		try {
			Long id = Long.valueOf(request.getParameter("id"));
			String password = request.getParameter("password");
			// 6位随机数
            String randomNum = "";
            for (int i = 0; i < 6; i++) {
                randomNum += (CommUtil.randomRang(0, 9));
            }
			if (id != null) {
				if (StringUtils.isNotBlank(password)) {
					Map<String,Object> paramMap = new HashMap<String,Object>();
					paramMap.put("id", id);
					paramMap.put("password", DigestUtils.md5Hex(password + randomNum));
					paramMap.put("randomNum", randomNum);
					result = giftCustomerManageService.updateGiftCustomerPassWord(paramMap);
				}
			}
		} catch (Exception e) {
			LOGGER.error("save", e);
		}
		super.printJson(response, result.toJsonString());
	}
    
    /**
     * 用户唯一性
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "verify_user.htm", method = RequestMethod.POST)
	public void verifyUser(HttpServletRequest request, HttpServletResponse response) {
		String uName = request.getParameter("userName");
		boolean exist = giftCustomerManageService.queryExistGiftCustomer(uName);
		if (exist) {
			printJson(response, "true");
		} else {
			printJson(response, "false");
		}
	}
    
    /**
     * 删除客户
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "delete.htm", method = RequestMethod.POST)
	@SysLog(title = "删除客户", value = "/gps/customer/delete.htm", description = "删除客户")
	public void delete(HttpServletRequest request, HttpServletResponse response) {
    	 BaseResult br = new BaseResult();
         try {
             String ids = request.getParameter("ids");
             List<Long> idList = new ArrayList<Long>();
             for (String id : ids.split(",")) {
                 idList.add(Long.parseLong(id));
             }
             br = giftCustomerManageService.deleteById(idList,generageRequestId());
         } catch (Exception e) {
             LOGGER.error("delete", e);
             br.setErrorMessage("99", "删除失败");
         }
         printJson(response, br.toJsonString());
	}
}
