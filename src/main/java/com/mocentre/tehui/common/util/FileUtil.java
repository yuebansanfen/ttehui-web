package com.mocentre.tehui.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class FileUtil {

    private static Logger LOGGER = Logger.getLogger(FileUtil.class);

    /**
     * 文件下载或在线打开
     * 
     * @param filePath 文件路径
     * @param response
     * @param isOnLine 是否在线打开
     */
    public static void download(String filePath, HttpServletResponse response, boolean isOnLine) {
        BufferedInputStream in = null;
        OutputStream out = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                response.sendError(404, "File not found!");
                return;
            }
            in = new BufferedInputStream(new FileInputStream(file));
            out = response.getOutputStream();
            byte[] buf = new byte[1024];
            int len = 0;
            response.reset();
            String fileName = file.getName();
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            if (isOnLine) { // 在线打开方式
                URL u = new URL("file:///" + filePath);
                response.setContentType(u.openConnection().getContentType());
                response.setHeader("Content-Disposition", "inline; filename=" + fileName);
            } else { // 纯下载方式
                // response.setContentType("application/x-msdownload");
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                String length = String.valueOf(file.length());
                response.setHeader("Content-Length", length);
            }
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            LOGGER.error("dowload", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 年月日文件夹 eg:年/月/日
     * 
     * @param path 绝对路径
     * @return
     */
    public static String getYMDFile(String path) {
        if (path == null || "".equals(path))
            return null;
        String ymd = null;
        try {
            if (!path.endsWith("/") && !path.endsWith(File.separator)) {
                path = path + File.separator;
            }
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DATE);
            ymd = year + File.separator + month + File.separator + day;
            File file = new File(path + ymd);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            ymd = null;
            LOGGER.error("getYMDFile()", e);
        }
        return ymd;
    }

    /**
     * 文件复制
     * 
     * @param sourceFile 原文件
     * @param targetFile 目标文件
     * @throws IOException
     */
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            outBuff.flush();
        } finally {
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

}
