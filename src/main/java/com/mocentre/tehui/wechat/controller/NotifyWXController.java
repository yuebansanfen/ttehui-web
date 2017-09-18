package com.mocentre.tehui.wechat.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mocentre.common.BaseResult;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.frontend.service.OrderManageService;

@Controller
@RequestMapping("/wxa/notify")
public class NotifyWXController extends BaseController {

    @Autowired
    private OrderManageService orderMagService;

    @RequestMapping(value = "async.htm", method = RequestMethod.POST)
    public void wxAsync(HttpServletRequest request, HttpServletResponse response) {
        String resData = "";
        StringBuffer bufData = new StringBuffer();
        BufferedReader in = null;
        String inputLine = null;
        try {
            in = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            while ((inputLine = in.readLine()) != null) {
                bufData.append(inputLine);
            }
            BaseResult backRes = orderMagService.wxaAsyncNotify(bufData.toString());
            if (backRes.isSuccess()) {
                resData = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            } else {
                resData = "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
            }
        } catch (Exception e) {
            resData = "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        super.printXML(response, resData);
    }
}
