package com.mocentre.tehui.common.enmu;

/**
 * 图片type及图片大小对应信息
 * Created by 王雪莹 on 2016/12/5.
 */
public enum ImgType {

    /*NONE*/
    NONE("base",null,null,null),

    /* 商品logo */
    SHOP_LOGO("shop_root",null,null,null),

    /* 店铺列表页图片 */
    GOODS_LISTPAGE("hhhhh",null,null,null),

    /* 商品购物车图片*/
    GOODS_CART("444---",88,999,000);

    private String rootPath ;
    private Integer width;
    private Integer height;
    private Integer size;

    private ImgType(String rootPath,
                    Integer width,
                    Integer height,
                    Integer size){
        this.rootPath=rootPath;
        this.width=width;
        this.height=height;
        this.size=size;
    }

    public String getRootPath() {
        return rootPath;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getSize() {
        return size;
    }
}
