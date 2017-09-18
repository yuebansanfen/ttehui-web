/*
 * Copyright 2016 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.mocentre.tehui.common.SystemConfig;
import com.mocentre.tehui.common.constant.ConfigConstant;
import com.mocentre.tehui.common.enmu.ImgType;
import com.mocentre.tehui.common.util.FileUtil;
import com.mocentre.tehui.core.controller.BaseController;
import com.mocentre.tehui.core.utils.response.PlainResult;

/**
 * 类UploadController.java的实现描述：上传图片控制器
 * 
 * @author sz.gong 2016年11月24日 下午3:48:03
 */
@Controller
@RequestMapping("/common/upload")
public class UploadController extends BaseController {

    /**
     * 上传文件
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "upload.htm", method = RequestMethod.POST)
    public void upload(HttpServletRequest request, HttpServletResponse response) {

        PlainResult<Map<String, Object>> br = new PlainResult<Map<String, Object>>();
        Map<String, Object> brMap = new HashMap<String, Object>();
        try {
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession()
                    .getServletContext());
            multipartResolver.setDefaultEncoding("utf-8");
            multipartResolver.setUploadTempDir(new FileSystemResource("fileUpload/temp"));
            //MultipartHttpServletRequest multiRequest = multipartResolver.resolveMultipart(request);
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            String type = multiRequest.getParameter("type");
            String root = null;
            if ("shop".equals(type)) {//不同类型
                root = SystemConfig.INSTANCE.getValue(ConfigConstant.SHOP_ROOT);
            } else if ("discover".equals(type)) {
                root = SystemConfig.INSTANCE.getValue(ConfigConstant.DISC_ROOT);
            } else if ("subject".equals(type)) {
                root = SystemConfig.INSTANCE.getValue(ConfigConstant.SUB_ROOT);
            }
            String nStatic = SystemConfig.INSTANCE.getValue(ConfigConstant.N_STATIC);
            String sUrl = SystemConfig.INSTANCE.getValue(ConfigConstant.STATIC_URL);
            String relPath = null, rootPath = null;
            root += File.separator;
            nStatic += File.separator;
            sUrl += File.separator;
            relPath = FileUtil.getYMDFile(root);
            rootPath = root + relPath;
            if (!relPath.endsWith("/") && !relPath.endsWith(File.separator)) {
                relPath += File.separator;
            }
            if (!rootPath.endsWith("/") && !rootPath.endsWith(File.separator)) {
                rootPath += File.separator;
            }
            int width = 0, height = 0;
            if (multipartResolver.isMultipart(request)) {
                Iterator<String> iter = multiRequest.getFileNames();
                while (iter.hasNext()) {
                    MultipartFile file = multiRequest.getFile(iter.next());
                    if (file != null) {
                        String myFileName = new Date().getTime() + "_" + file.getOriginalFilename();
                        if (!"".equals(myFileName.trim())) {
                            BufferedImage bi = ImageIO.read(file.getInputStream());
                            long size = file.getSize();
                            if ("shop".equals(type)) {//不同类型判断
                                width = bi.getWidth();
                                height = bi.getHeight();
                                if (size > 1000 * 1000 * 1000) {
                                    br.setErrorMessage("100", "图片太大,最多允许上传1000kb,请重新上传");
                                    break;
                                }
                            } else if ("discover".equals(type)) {
                                width = bi.getWidth();
                                height = bi.getHeight();
                                if (width != 750 || height != 1280) {
                                    br.setErrorMessage("101", "图片尺寸错误,请重新上传");
                                    break;
                                }
                            } else if ("subject".equals(type)) {

                            }
                            String fileName = myFileName;
                            String path = rootPath + fileName;
                            File localFile = new File(path);
                            file.transferTo(localFile);
                            String imgUrl = sUrl + nStatic + relPath + fileName;
                            brMap.put("url", imgUrl);
                            brMap.put("width", width);
                            brMap.put("height", height);
                            br.setData(brMap);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("upload failed", e);
        }
        printJson(response, br.toJsonString());
    }

    /**
     * 上传文件
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "uploadImg.htm", method = RequestMethod.POST)
    public void uploadImg(HttpServletRequest request, HttpServletResponse response) {

        PlainResult<Map<String, Object>> br = new PlainResult<Map<String, Object>>();
        Map<String, Object> brMap = new HashMap<String, Object>();
        try {
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession()
                    .getServletContext());
            multipartResolver.setDefaultEncoding("utf-8");
            multipartResolver.setUploadTempDir(new FileSystemResource("fileUpload/temp"));
            //MultipartHttpServletRequest multiRequest = multipartResolver.resolveMultipart(request);
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            String type = multiRequest.getParameter("type");
            if(type == "" || type == null){
                type="NONE";
            }
            ImgType imgType = ImgType.valueOf(type);
            String root = imgType.getRootPath();
            String nStatic = SystemConfig.INSTANCE.getValue(ConfigConstant.N_STATIC);
            String sUrl = SystemConfig.INSTANCE.getValue(ConfigConstant.STATIC_URL);
            String relPath = null, rootPath = null;
            root += File.separator;
            nStatic += File.separator;
            sUrl += File.separator;
            relPath = FileUtil.getYMDFile(root);
            rootPath = root + relPath;
            if (!relPath.endsWith("/") && !relPath.endsWith(File.separator)) {
                relPath += File.separator;
            }
            if (!rootPath.endsWith("/") && !rootPath.endsWith(File.separator)) {
                rootPath += File.separator;
            }
            int width = 0, height = 0;
            if (multipartResolver.isMultipart(request)) {
                Iterator<String> iter = multiRequest.getFileNames();
                while (iter.hasNext()) {
                    MultipartFile file = multiRequest.getFile(iter.next());
                    if (file != null) {
                        String myFileName = new Date().getTime() + "_" + file.getOriginalFilename();
                        if (!"".equals(myFileName.trim())) {
                            BufferedImage bi = ImageIO.read(file.getInputStream());
                            long size = file.getSize();
                            if (imgType.getSize() != null && size > imgType.getSize()) {
                                br.setErrorMessage("100", "图片太大,最多允许上传" + imgType.getSize() + "kb,请重新上传");
                                break;
                            }
                            if (imgType.getHeight() != null && bi.getHeight() == imgType.getHeight()) {
                                br.setErrorMessage("100", "图片尺寸错误,请上传高" + imgType.getHeight() + "px的图片");
                                break;
                            }
                            if (imgType.getWidth() != null && bi.getWidth() == imgType.getWidth()) {
                                br.setErrorMessage("100", "图片尺寸错误,请上传宽" + imgType.getWidth() + "px的图片");
                                break;
                            }
                            String fileName = myFileName;
                            String path = rootPath + fileName;
                            File localFile = new File(path);
                            file.transferTo(localFile);
                            String imgUrl = sUrl + nStatic + relPath + fileName;
                            brMap.put("url", imgUrl);
                            brMap.put("width", width);
                            brMap.put("height", height);
                            br.setData(brMap);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("upload failed", e);
        }
        printJson(response, br.toJsonString());
    }
    /**
     * 上传kindeditor图片文件
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "uploadKEImg.htm", method = RequestMethod.POST)
    public void uploadKEImg(HttpServletRequest request, HttpServletResponse response) {
        String imgUrl = "";
        try {
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession()
                    .getServletContext());
            multipartResolver.setDefaultEncoding("utf-8");
            multipartResolver.setUploadTempDir(new FileSystemResource("fileUpload/temp"));
            //MultipartHttpServletRequest multiRequest = multipartResolver.resolveMultipart(request);
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            String type = multiRequest.getParameter("type");
            if(type == "" || type == null){
                type="NONE";
            }
            ImgType imgType = ImgType.valueOf(type);
            String root = imgType.getRootPath();
            String nStatic = SystemConfig.INSTANCE.getValue(ConfigConstant.N_STATIC);
            String sUrl = SystemConfig.INSTANCE.getValue(ConfigConstant.STATIC_URL);
            String relPath = null, rootPath = null;
            root += File.separator;
            nStatic += File.separator;
            sUrl += File.separator;
            relPath = FileUtil.getYMDFile(root);
            rootPath = root + relPath;
            if (!relPath.endsWith("/") && !relPath.endsWith(File.separator)) {
                relPath += File.separator;
            }
            if (!rootPath.endsWith("/") && !rootPath.endsWith(File.separator)) {
                rootPath += File.separator;
            }
            int width = 0, height = 0;
            if (multipartResolver.isMultipart(request)) {
                Iterator<String> iter = multiRequest.getFileNames();
                while (iter.hasNext()) {
                    MultipartFile file = multiRequest.getFile(iter.next());
                    if (file != null) {
                        String myFileName = new Date().getTime() + "_" + file.getOriginalFilename();
                        if (!"".equals(myFileName.trim())) {
                            BufferedImage bi = ImageIO.read(file.getInputStream());
                            long size = file.getSize();
                            if(imgType.getSize() != null && size >imgType.getSize()){
                                break;
                            }
                            if(imgType.getHeight() != null && bi.getHeight()==imgType.getHeight()){
                                break;
                            }
                            if(imgType.getWidth() != null && bi.getWidth()==imgType.getWidth()){
                                break;
                            }
                            String fileName = myFileName;
                            String path = rootPath + fileName;
                            File localFile = new File(path);
                            file.transferTo(localFile);
                            imgUrl = sUrl + nStatic + relPath + fileName;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("upload failed", e);
        }
        Map<String, Object> succMap = new HashMap<String, Object>();
        succMap.put("error", 0);
        succMap.put("url",imgUrl);
        printJson(response, JSON.toJSONString(succMap));
    }
}
