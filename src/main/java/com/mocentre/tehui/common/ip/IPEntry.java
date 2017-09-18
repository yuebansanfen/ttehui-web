package com.mocentre.tehui.common.ip;

/**
 * 类IPEntry.java的实现描述：纯真ip查询，该类用来读取QQWry.dat中的的IP记录信息 </p>
 * 
 * @author sz.gong 2016年3月13日 下午5:24:52
 */
public class IPEntry {
    public String beginIp;
    public String endIp;
    public String country;
    public String area;

    /**
     * 14. * 构造函数
     */
    public IPEntry() {
        beginIp = endIp = country = area = "";
    }

}
