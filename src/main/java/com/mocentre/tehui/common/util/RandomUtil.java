package com.mocentre.tehui.common.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * @Author wangkuiqing
 * @Date 2016/8/3 9:03
 * @ClassName:RandomUtils
 * @Description:用于随机生成动态验证码
 */
public class RandomUtil {
    private static Random             random;
    private static Point              p1, p2, p3, p4, p5, p6, p7, p8, p9;
    private static ArrayList<Point>   plist     = new ArrayList<Point>();
    private static Map<Point, String> pmap      = new HashMap<Point, String>();
    private static ArrayList<Color>   colorlist = new ArrayList<Color>();
    private static double             xAdd      = 80;
    private static double             yAdd      = 81;
    static {
        init();
    }

    private static void init() {
        // 生成座标
        p1 = new Point(40, 39);
        p2 = new Point(120, 39);
        p3 = new Point(200, 39);
        p4 = new Point(40, 120);
        p5 = new Point(120, 120);
        p6 = new Point(200, 120);
        p7 = new Point(40, 201);
        p8 = new Point(120, 201);
        p9 = new Point(200, 201);

        // 将圆心座标存放到plist中。
        plist.add(p1);
        plist.add(p2);
        plist.add(p3);
        plist.add(p4);
        plist.add(p5);
        plist.add(p6);
        plist.add(p7);
        plist.add(p8);
        plist.add(p9);

        pmap.put(p1, "1");
        pmap.put(p2, "2");
        pmap.put(p3, "3");
        pmap.put(p4, "4");
        pmap.put(p5, "5");
        pmap.put(p6, "6");
        pmap.put(p7, "7");
        pmap.put(p8, "8");
        pmap.put(p9, "9");
        colorlist.add(new Color(36, 125, 198));
    }

    public static int nextInt() {
        return random.nextInt();
    }

    public static int nextInt(int n) {
        return random.nextInt(n);
    }

    /**
     * 输出验证码图片
     *
     * @param os 输出流
     * @param code 图片上的验证码
     * @param width 图片宽度
     * @param height 图片高度
     * @throws IOException
     */
    public static StringBuffer generateVerifyCode(OutputStream os, String imageDir) throws IOException {
        BufferedImage buffImg = ImageIO.read(new FileInputStream(imageDir));
        StringBuffer strBuffer = new StringBuffer();
        ArrayList<Point> flist = new ArrayList<Point>();
        ArrayList<Point> hlist = new ArrayList<Point>();
        hlist.addAll(plist);
        //生成轨迹点
        int index = CommUtil.randomRang(0, 8);
        Point point = plist.get(index);
        flist.add(point);
        hlist.remove(point);
        for (int i = 0; i < 6; i++) {
            ArrayList<Point> pRang = createNextPointRang(point, hlist);
            if (pRang.size() == 0) {
                break;
            } else if (pRang.size() == 1) {
                index = 0;
            } else {
                index = CommUtil.randomRang(0, pRang.size() - 1);
            }
            point = pRang.get(index);
            flist.add(point);
            hlist.remove(point);
        }
        //创建绘图类
        Graphics2D g = buffImg.createGraphics();
        //设置颜色
        g.setColor(colorlist.get(0));
        //抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //设置arrow的粗线
        float lineWidth = 2.0f;
        g.setStroke(new BasicStroke(lineWidth));
        //初始化参数
        int x = 0;
        int y = 0;
        int x1 = 0;
        int y1 = 0;
        int x2 = 0;
        int y2 = 0;
        int x3 = 0;
        int y3 = 0;
        // 箭头高度
        double H = 10;
        // 底边的一半
        double L = 7;
        // 箭头角度
        double awrad = Math.atan(L / H);
        // 箭头的长度
        double arraow_len = Math.sqrt(L * L + H * H);
        double[] arrXY_1 = rotateVec(x1 - x, y1 - y, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(x1 - x, y1 - y, -awrad, true, arraow_len);
        //开始画线
        int size = flist.size();
        for (int ii = 0; ii < size - 1; ii++) {
            Point ap = flist.get(ii);
            Point bp = flist.get(ii + 1);

            x = new Double(ap.getX()).intValue();
            y = new Double(ap.getY()).intValue();
            x1 = new Double(bp.getX()).intValue();
            y1 = new Double(bp.getY()).intValue();
            arrXY_1 = rotateVec(x1 - x, y1 - y, awrad, true, arraow_len);
            arrXY_2 = rotateVec(x1 - x, y1 - y, -awrad, true, arraow_len);
            x2 = new Double(x1 - arrXY_1[0]).intValue();
            y2 = new Double(y1 - arrXY_1[1]).intValue();
            x3 = new Double(x1 - arrXY_2[0]).intValue();
            y3 = new Double(y1 - arrXY_2[1]).intValue();

            g.fillOval(x - 5, y - 5, 10, 10);
            //画线
            g.drawLine(x, y, x1, y1);
            // 画箭头的一半
            g.drawLine(x1, y1, x2, y2);
            // 画箭头的另一半
            g.drawLine(x1, y1, x3, y3);

            strBuffer.append(pmap.get(ap));
        }
        strBuffer.append(pmap.get(flist.get(size - 1)));

        ImageIO.write(buffImg, "jpeg", os);
        os.flush();

        return strBuffer;
    }

    private static double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen) {
        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }

    /**
     * 获取当前点的下一个点的生成范围
     * 
     * @return
     */
    private static ArrayList<Point> createNextPointRang(Point p, ArrayList<Point> hlist) {
        ArrayList<Point> nplist = new ArrayList<Point>();
        ArrayList<Point> tpList = createAllPoint(p);

        for (Point tp : tpList) {
            if (isHavePoint(tp, hlist)) {
                nplist.add(tp);
            }
        }

        return nplist;
    }

    /**
     * 判断生成点是否放入最终生成范围
     * 
     * @param tp
     * @return
     */
    private static boolean isHavePoint(Point tp, ArrayList<Point> hlist) {
        boolean flag = false;
        for (Point p : hlist) {
            if (p.getX() == tp.getX() && p.getY() == tp.getY()) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 生成所有可能点
     * 
     * @param p
     * @return
     */
    private static ArrayList<Point> createAllPoint(Point p) {
        ArrayList<Point> tplist = new ArrayList<Point>();
        ArrayList<Double> xlist = new ArrayList<Double>();
        ArrayList<Double> ylist = new ArrayList<Double>();

        double newx1 = p.getX();
        double newx2 = newx1 + xAdd;
        double newx3 = newx1 - xAdd;
        xlist.add(newx1);
        xlist.add(newx2);
        xlist.add(newx3);

        double newy1 = p.getY();
        double newy2 = newy1 + yAdd;
        double newy3 = newy1 - yAdd;
        ylist.add(newy1);
        ylist.add(newy2);
        ylist.add(newy3);

        for (int i = 0; i < xlist.size(); i++) {
            for (int j = 0; j < ylist.size(); j++) {
                Point tp = new Point();
                if (i != 0 || j != 0) {
                    tp.setLocation(xlist.get(i), ylist.get(j));
                    tplist.add(tp);
                }
            }
        }

        return tplist;
    }
}
