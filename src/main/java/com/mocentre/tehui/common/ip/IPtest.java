package com.mocentre.tehui.common.ip;


/**
 * 类IPtest.java的实现描述：test
 * 
 * @author sz.gong 2016年3月13日 下午5:25:38
 */
public class IPtest {
    public static void main(String[] args) {
        // 指定纯真数据库的文件名，所在文件夹
        IPSeeker ip = new IPSeeker("QQWry.Dat", "f:/");
        //        //String temp = "169.254.111.173";
        String temp = "58.20.43.13";
        // 测试IP 58.20.43.13 218.30.180.177
        IPLocation ipLocal = ip.getIPLocation(temp);
        System.out.println(ipLocal.getCountry());
        System.out.println(ipLocal.getArea());
    }

}
