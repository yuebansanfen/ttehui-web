/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 * 类FtpUtil.java的实现描述：ftp操作
 * 
 * @author sz.gong 2017年6月10日 下午5:34:55
 */
public class FtpUtil {

    private static Logger LOGGER = Logger.getLogger(FtpUtil.class);

    private static String OS     = System.getProperty("os.name").toLowerCase();
    private FTPClient     ftpClient;
    private String        hostname;
    private int           port;
    private String        username;
    private String        password;
    private String        root;

    /**
     * @param hostname 域名或ip
     * @param port 端口
     * @param username 用户
     * @param password 密码
     * @param root 根目录
     */
    public FtpUtil(String hostname, int port, String username, String password, String root) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.root = root;
        ftpClient = getClient();
    }

    public boolean ftpLogin() {
        boolean isLogin = false;
        this.ftpClient.setControlEncoding("GBK");
        try {
            if (this.port > 0) {
                this.ftpClient.connect(this.hostname, this.port);
            } else {
                this.ftpClient.connect(this.hostname);
            }
            // FTP服务器连接回答
            int reply = this.ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                this.ftpClient.disconnect();
                return isLogin;
            }
            this.ftpClient.login(this.username, this.password);
            // 设置传输协议
            this.ftpClient.enterLocalPassiveMode();
            this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            isLogin = true;
        } catch (Exception e) {
            isLogin = false;
            return isLogin;

        }
        this.ftpClient.setBufferSize(1024 * 2);
        //this.ftpClient.setDataTimeout(30 * 1000);
        return isLogin;
    }

    /**
     * @退出关闭服务器链接
     */
    public void ftpLogOut() {
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                boolean reuslt = this.ftpClient.logout();// 退出FTP服务器
                if (reuslt) {
                }
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                try {
                    this.ftpClient.disconnect();// 关闭FTP服务器的连接
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    public boolean uploadFile(InputStream inputStream, String remoteUpLoadPath, String fileName) {
        BufferedInputStream inStream = null;
        boolean success = false;
        try {
            ftpClient.changeWorkingDirectory(root);
            //ftpClient.makeDirectory(remoteUpLoadPath);
            createDirs(remoteUpLoadPath);
            //this.ftpClient.changeWorkingDirectory(remoteUpLoadPath);// 改变工作路径
            inStream = new BufferedInputStream(inputStream);
            success = this.ftpClient.storeFile(fileName, inStream);
            if (success == true) {
                return success;
            }
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return success;
    }

    public ByteArrayOutputStream downloadFile(String remoteDownLoadPath, String remoteFileName) {
        ByteArrayOutputStream outStream = null;
        boolean success = false;
        try {
            //this.ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            outStream = new ByteArrayOutputStream();
            if ("/".equals(root)) {
                success = this.ftpClient.retrieveFile(remoteDownLoadPath + remoteFileName, outStream);
            } else {
                success = this.ftpClient.retrieveFile(root + remoteDownLoadPath + remoteFileName, outStream);
            }
            if (success == true) {
                return outStream;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (null != outStream) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        if (success == false) {
        }
        return null;
    }

    private FTPClient getClient() {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setDefaultPort(port);
        ftpClient.configure(getClientConfig());
        ftpClient.setControlEncoding("GBK");
        return ftpClient;
    }

    private static FTPClientConfig getClientConfig() {
        String sysType = null;
        if (isLinux()) {
            sysType = FTPClientConfig.SYST_UNIX;
        } else if (isWindows()) {
            sysType = FTPClientConfig.SYST_NT;
        }
        FTPClientConfig config = new FTPClientConfig(sysType);
        config.setRecentDateFormatStr("yyyy-MM-dd HH:mm");
        return config;
    }

    public void createDirs(String remoteUpLoadPath) throws IOException {
        String[] dirs = remoteUpLoadPath.split("/");
        for (String dir : dirs) {
            this.ftpClient.mkd(dir);
            this.ftpClient.changeWorkingDirectory(dir);
        }
    }

    private static boolean isLinux() {
        return OS.indexOf("linux") >= 0;
    }

    private static boolean isWindows() {
        return OS.indexOf("windows") >= 0;
    }

    public static void main(String[] args) {
        FtpUtil ftpUtil = new FtpUtil("static.mocentre.com", 21, "staticmocentre/staticflie", "mocentre,32145mo", "/");
        boolean suc = ftpUtil.ftpLogin();
        System.out.println(suc);
        File file = new File(
                "E:\\IDE-maven\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp6\\wtpwebapps\\ttehui-web\\json\\mallHome.json");
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            ftpUtil.uploadFile(inputStream, "/staticflie/ttehui_static_abcbank/alpha/static/json", "aaa.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ftpUtil.ftpLogOut();
    }

}
