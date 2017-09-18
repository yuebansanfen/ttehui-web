/*
 * Copyright 2017 mocentre.com All right reserved. This software is the
 * confidential and proprietary information of mocentre.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with mocentre.com .
 */
package com.mocentre.tehui.common.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 类IPDataHandler.java的实现描述：通过17monipdb库，查询省市区
 * 
 * @author sz.gong 2017年4月5日 下午6:30:09
 */
public final class IPDataHandler {

    //文件名称
    private static String       IP_FILE     = "17monipdb.dat";
    // 保存的文件夹
    private String              INSTALL_DIR = getClass().getClassLoader().getResource("").getPath();
    private DataInputStream     inputStream = null;
    private static long         fileLength  = -1;
    private static int          dataLength  = -1;
    private Map<String, String> cacheMap    = null;
    private byte[]              allData     = null;
    private String              country     = null;
    private String              provice     = null;
    private String              city        = null;

    public IPDataHandler() {
        File file = new File(INSTALL_DIR + File.separator + IP_FILE);
        try {
            inputStream = new DataInputStream(new FileInputStream(file));
            fileLength = file.length();
            cacheMap = new HashMap<String, String>();
            if (fileLength > Integer.MAX_VALUE) {
                throw new Exception("the filelength over 2GB");
            }
            dataLength = (int) fileLength;
            allData = new byte[dataLength];
            inputStream.read(allData, 0, dataLength);
            dataLength = (int) getbytesTolong(allData, 0, 4, ByteOrder.BIG_ENDIAN);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long getbytesTolong(byte[] bytes, int offerSet, int size, ByteOrder byteOrder) {
        if ((offerSet + size) > bytes.length || size <= 0) {
            return -1;
        }
        byte[] b = new byte[size];
        for (int i = 0; i < b.length; i++) {
            b[i] = bytes[offerSet + i];
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(b);
        byteBuffer.order(byteOrder);

        long temp = -1;
        if (byteBuffer.hasRemaining()) {
            temp = byteBuffer.getInt();
        }
        return temp;
    }

    private long ip2long(String ip) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(ip);
        byte[] bytes = address.getAddress();
        long reslut = getbytesTolong(bytes, 0, 4, ByteOrder.BIG_ENDIAN);
        return reslut;
    }

    private int getIntByBytes(byte[] b, int offSet) {
        if (b == null || (b.length < (offSet + 3))) {
            return -1;
        }

        byte[] bytes = Arrays.copyOfRange(allData, offSet, offSet + 3);
        byte[] bs = new byte[4];
        bs[3] = 0;
        for (int i = 0; i < 3; i++) {
            bs[i] = bytes[i];
        }

        return (int) getbytesTolong(bs, 0, 4, ByteOrder.LITTLE_ENDIAN);
    }

    public String findGeography(String address) {
        if (StringUtils.isBlank(address)) {
            //return "illegal address";
            return null;
        }

        if (dataLength < 4 || allData == null) {
            //return "illegal ip data";
            return null;
        }

        String ip = "127.0.0.1";
        try {
            ip = Inet4Address.getByName(address).getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        String[] ipArray = StringUtils.split(ip, ".");
        int ipHeadValue = Integer.parseInt(ipArray[0]);
        if (ipArray.length != 4 || ipHeadValue < 0 || ipHeadValue > 255) {
            //return "illegal ip";
            return null;
        }

        if (cacheMap.containsKey(ip)) {
            return cacheMap.get(ip);
        }

        long numIp = 1;
        try {
            numIp = ip2long(address);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }

        int tempOffSet = ipHeadValue * 4 + 4;
        long start = getbytesTolong(allData, tempOffSet, 4, ByteOrder.LITTLE_ENDIAN);
        int max_len = dataLength - 1028;
        long resultOffSet = 0;
        int resultSize = 0;

        for (start = start * 8 + 1024; start < max_len; start += 8) {
            if (getbytesTolong(allData, (int) start + 4, 4, ByteOrder.BIG_ENDIAN) >= numIp) {
                resultOffSet = getIntByBytes(allData, (int) (start + 4 + 4));
                resultSize = (char) allData[(int) start + 7 + 4];
                break;
            }
        }

        if (resultOffSet <= 0) {
            //return "resultOffSet too small";
            return null;
        }

        byte[] add = Arrays.copyOfRange(allData, (int) (dataLength + resultOffSet - 1024), (int) (dataLength
                + resultOffSet - 1024 + resultSize));
        try {
            if (add == null) {
                cacheMap.put(ip, new String("no data found!!"));
            } else {
                cacheMap.put(ip, new String(add, "UTF-8"));
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return cacheMap.get(ip);
    }

    public void findIPLocation(String address) {
        String ipLoca = this.findGeography(address);
        if (ipLoca != null) {
            String[] loca = ipLoca.split("\t");
            this.country = loca.length > 0 ? loca[0] : null;
            this.provice = loca.length > 1 ? loca[1] : null;
            this.city = loca.length > 2 ? loca[2] : null;
        }
    }

    public String getCountry() {
        return country;
    }

    public String getProvice() {
        return provice;
    }

    public String getCity() {
        return city;
    }

}
