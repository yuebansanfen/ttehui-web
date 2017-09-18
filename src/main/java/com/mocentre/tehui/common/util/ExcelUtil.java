package com.mocentre.tehui.common.util;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * excel导出工具类
 *
 * @author liqifan
 * @ClassName: ExcelUtil
 * @date 2016年7月1日 上午11:05:01
 */
public class ExcelUtil {
    private static XSSFWorkbook wb;

    private static CellStyle titleStyle; // 标题行样式
    private static Font titleFont; // 标题行字体
    private static CellStyle dateStyle; // 日期行样式
    private static Font dateFont; // 日期行字体
    private static CellStyle headStyle; // 表头行样式
    private static Font headFont; // 表头行字体
    private static CellStyle contentStyle; // 内容行样式
    private static Font contentFont; // 内容行字体

    private static int columNum = 0;

    /**
     * 导出excel
     *
     * @param xssMap
     * @return
     * @throws Exception
     */
    public static XSSFWorkbook exportExcelFile(Map<String, Object> xssMap) throws Exception {

        init();
        Iterator<String> keys = xssMap.keySet().iterator();
        while (keys.hasNext()){
            columNum = 0;
            String key = keys.next();
            XSSFSheet sheet = wb.createSheet(key);
            sheet.setDefaultColumnWidth(40);
            //autoSize(sheet);
            ExcelExportData exportData = (ExcelExportData)xssMap.get(key);
            createTableDetail(exportData, sheet);
            createTableTitleRow(exportData, sheet);
            creatTableColumns(exportData, sheet);
        }
//        autoSize(sheet);
        return wb;
    }

    /**
     * @Description: 初始化
     */
    private static void init() {
        wb = new XSSFWorkbook();

        titleFont = wb.createFont();
        titleStyle = wb.createCellStyle();
        dateStyle = wb.createCellStyle();
        dateFont = wb.createFont();
        headStyle = wb.createCellStyle();
        headFont = wb.createFont();
        contentStyle = wb.createCellStyle();
        contentFont = wb.createFont();

        initTitleCellStyle();
        initTitleFont();
        initDateCellStyle();
        initDateFont();
        initHeadCellStyle();
        initHeadFont();
        initContentCellStyle();
        initContentFont();
    }

    /**
     * @Description: 创建附加信息
     */
    private static void createTableDetail(ExcelExportData setInfo, XSSFSheet sheet) {
        Map<String, Object> tableDetail = setInfo.getTableDetail();
        if (tableDetail != null && tableDetail.size() > 0){
            Iterator keyValue = tableDetail.entrySet().iterator();
            for (int i = 0; i < tableDetail.size(); i++) {
                Map.Entry entry = (Map.Entry) keyValue.next();
                XSSFRow row = sheet.createRow(columNum++);
                XSSFCell cell = row.createCell(0);
                cell.setCellStyle(headStyle);
                cell.setCellValue(entry.getKey().toString());
                cell = row.createCell(1);
                cell.setCellStyle(headStyle);
                cell.setCellValue(entry.getValue().toString());
            }
        }
    }

    /**
     * @Description: 创建标题行
     */
    private static void createTableTitleRow(ExcelExportData setInfo, XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(columNum++);
        String[] title = setInfo.getTitles();
        if (title != null && title.length > 0){
            for (int i = 0; i < title.length; i++) {
                XSSFCell titleCell = row.createCell(i);
                titleCell.setCellStyle(titleStyle);
                titleCell.setCellValue(title[i]);
            }
        }
    }

    /**
     * @Description: 自动调整所有列宽
     */
    private static void autoSize(XSSFSheet sheet) {
        int coloumNum = sheet.getRow(0).getPhysicalNumberOfCells();
        for (int i = 0; i < coloumNum; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * @Description: 创建内容行的每一列
     */
    private static void creatTableColumns(ExcelExportData setInfo, XSSFSheet sheet) {
        List<String[]> columns = setInfo.getColumns();
        if (columns != null && columns.size() > 0){
            for (int i = 0; i < columns.size(); i++) {
                XSSFRow row = sheet.createRow(columNum++);
                String[] column = columns.get(i);
                for (int j = 0; j < column.length; j++) {
                    XSSFCell columnCell = row.createCell(j);
                    columnCell.setCellValue(column[j]);
                    columnCell.setCellStyle(contentStyle);
                }
            }
        }
    }

    /**
     * @Description: 初始化标题行样式
     */
    private static void initTitleCellStyle() {
        titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        titleStyle.setFont(titleFont);
        titleStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.getIndex());
        titleStyle.setAlignment(CellStyle.ALIGN_CENTER); // 创建一个居中格式
        titleStyle.setBorderBottom(CellStyle.BORDER_THIN); //下边框
        titleStyle.setBorderLeft(CellStyle.BORDER_THIN);//左边框
        titleStyle.setBorderTop(CellStyle.BORDER_THIN);//上边框
        titleStyle.setBorderRight(CellStyle.BORDER_THIN);//右边框

    }

    /**
     * @Description: 初始化日期行样式
     */
    private static void initDateCellStyle() {
        dateStyle.setAlignment(CellStyle.ALIGN_CENTER_SELECTION);
        dateStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        dateStyle.setFont(dateFont);
        dateStyle.setFillBackgroundColor(IndexedColors.SKY_BLUE.getIndex());
    }

    /**
     * @Description: 初始化表头行样式
     */
    private static void initHeadCellStyle() {
        headStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        headStyle.setFont(headFont);
        headStyle.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
//        headStyle.setBorderTop(CellStyle.BORDER_MEDIUM);
//        headStyle.setBorderBottom(CellStyle.BORDER_THIN);
//        headStyle.setBorderLeft(CellStyle.BORDER_THIN);
//        headStyle.setBorderRight(CellStyle.BORDER_THIN);
    }

    /**
     * @Description: 初始化内容行样式
     */
    private static void initContentCellStyle() {
        contentStyle.setAlignment(CellStyle.ALIGN_CENTER);
        contentStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        contentStyle.setFont(contentFont);
//        contentStyle.setBorderTop(CellStyle.BORDER_THIN);
//        contentStyle.setBorderBottom(CellStyle.BORDER_THIN);
//        contentStyle.setBorderLeft(CellStyle.BORDER_THIN);
//        contentStyle.setBorderRight(CellStyle.BORDER_THIN);
        contentStyle.setWrapText(true); // 字段换行
    }

    /**
     * @Description: 初始化标题行字体
     */
    private static void initTitleFont() {
        titleFont.setFontName("华文楷体");
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        titleFont.setCharSet(Font.DEFAULT_CHARSET);
        titleFont.setColor(IndexedColors.BLUE_GREY.getIndex());
    }

    /**
     * @Description: 初始化日期行字体
     */
    private static void initDateFont() {
        dateFont.setFontName("隶书");
        dateFont.setFontHeightInPoints((short) 10);
        dateFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        dateFont.setCharSet(Font.DEFAULT_CHARSET);
        dateFont.setColor(IndexedColors.BLUE_GREY.getIndex());
    }


    /**
     * @Description: 初始化表头行字体
     */
    private static void initHeadFont() {
        headFont.setFontName("宋体");
        headFont.setFontHeightInPoints((short) 14);
        headFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headFont.setCharSet(Font.DEFAULT_CHARSET);
        headFont.setColor(IndexedColors.BLUE_GREY.getIndex());
    }

    /**
     * @Description: 初始化内容行字体
     */
    private static void initContentFont() {
        contentFont.setFontName("宋体");
        contentFont.setFontHeightInPoints((short) 14);
        contentFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        contentFont.setCharSet(Font.DEFAULT_CHARSET);
        contentFont.setColor(IndexedColors.BLUE_GREY.getIndex());
    }

    /**
     * Excel导出数据类
     *
     * @author liqifan
     * @ClassName: ExcelExportData
     * @date 2016年7月1日 上午11:04:38
     */
    public static class ExcelExportData {

        /**
         * sheet附加内容
         */
        private Map<String, Object> tableDetail;

        /**
         * sheet标题
         */
        private String[] titles;

        /**
         * sheet内容
         */
        private List<String[]> columns;


        public Map<String, Object> getTableDetail() {
            return tableDetail;
        }

        public void setTableDetail(Map<String, Object> tableDetail) {
            this.tableDetail = tableDetail;
        }

        public String[] getTitles() {
            return titles;
        }

        public void setTitles(String[] titles) {
            this.titles = titles;
        }

        public List<String[]> getColumns() {
            return columns;
        }

        public void setColumns(List<String[]> columns) {
            this.columns = columns;
        }
    }
}