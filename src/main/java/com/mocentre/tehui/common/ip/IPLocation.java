package com.mocentre.tehui.common.ip;

/**
 * 类IPLocation.java的实现描述：纯真ip查询,该类用来读取地址信息
 * 
 * @author sz.gong 2016年3月13日 下午5:25:06
 */
public class IPLocation {
    private String country;
    private String area;

    public IPLocation() {
        country = area = "";
    }

    public IPLocation getCopy() {
        IPLocation ret = new IPLocation();
        ret.country = country;
        ret.area = area;
        return ret;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        // 如果为局域网，纯真IP地址库的地区会显示CZ88.NET,这里把它去掉
        if (area.trim().equals("CZ88.NET")) {
            this.area = "本机";
        } else {
            this.area = area;
        }
    }
}
