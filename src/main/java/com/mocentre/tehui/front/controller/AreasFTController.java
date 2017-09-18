package com.mocentre.tehui.front.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.ListResult;
import com.mocentre.common.MapResult;
import com.mocentre.common.PlainResult;
import com.mocentre.tehui.common.util.CommUtil;
import com.mocentre.tehui.common.util.IPDataHandler;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.model.AreasFTInstance;
import com.mocentre.tehui.frontend.service.AreasManageService;

/**
 * 微信活动接口 Created by yukaiji on 2016/01/22.
 */

@Controller
@RequestMapping("/front/areas")
public class AreasFTController extends BaseController {

    @Autowired
    private AreasManageService areasManageService;

    @RequestMapping(value = "getAreas.htm", method = RequestMethod.POST)
    public void getAreas(HttpServletRequest request, HttpServletResponse response) {
        ListResult<AreasFTInstance> result = new ListResult<AreasFTInstance>();
        try {
            result = areasManageService.getAllProvinceThreeLinkage(generageRequestId());
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    @RequestMapping(value = "ipCity.htm", method = RequestMethod.POST)
    public void ipCity(HttpServletRequest request, HttpServletResponse response) {
        MapResult result = new MapResult();
        Map<String, Object> resMap = new HashMap<String, Object>();
        try {
            AreasFTInstance cityIns = null;
            String ipAddress = CommUtil.getIpAddr(request);
            IPDataHandler ipData = new IPDataHandler();
            ipData.findIPLocation(ipAddress);
            String cityName = ipData.getCity();
            ListResult<AreasFTInstance> cityResultList = areasManageService.getCityList(generageRequestId());
            List<AreasFTInstance> cityList = cityResultList.getData();
            for (AreasFTInstance areas : cityList) {
                if (StringUtils.isNotBlank(cityName) && areas.getName().trim().equals(cityName.trim())) {
                    cityIns = new AreasFTInstance();
                    cityIns.setCode(areas.getCode());
                    cityIns.setName(areas.getName());
                    cityIns.setpCode(areas.getpCode());
                    break;
                }
            }
            //resMap.put("cityList", cityList);
            resMap.put("loca", cityIns);
            result.setRequestId(cityResultList.getRequestId());
            result.setData(resMap);
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

    @RequestMapping(value = "location.htm", method = RequestMethod.GET)
    public void location(HttpServletRequest request, HttpServletResponse response) {
        PlainResult<String> result = new PlainResult<>();
        try {
            String ipAddress = CommUtil.getIpAddr(request);
            IPDataHandler ipData = new IPDataHandler();
            ipData.findIPLocation(ipAddress);
            String city = ipData.getCity();
            result.setData(city);
        } catch (Exception e) {
            result.setErrorMessage("999", "系统错误");
        }
        super.printJson(response, result.toJsonString());
    }

}
