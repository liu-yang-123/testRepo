package com.zcxd.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.zcxd.common.util.excelStyle.ExcelMergeStrategy;
import com.zcxd.common.util.excelStyle.ExcelSheetStrategy;

// https://www.cnblogs.com/linjiqin/p/11065664.html
/**
 * EasyExcel 导入，导出工具类
 * @author
 * @create 2020/11/14
 */
public class EasyExcelUtil {

    public static List<String> fillTableColHead(String... heads) {
        return new ArrayList<>(Arrays.asList(heads));
    }
    /**
     * StringList 解析监听器
     */
    private static class StringExcelListener extends AnalysisEventListener {
        /**
         * 自定义用于暂时存储data
         * 可以通过实例获取该值
         */
        private List<List<String>> datas = new ArrayList<>();

        /**
         * 每解析一行都会回调invoke()方法
         *
         * @param object
         * @param context
         */
        @Override
        public void invoke(Object object, AnalysisContext context) {
            List<String> stringList= (List<String>) object;
            //数据存储到list，供批量处理，或后续自己业务逻辑处理。
            datas.add(stringList);
            //根据自己业务做处理
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            //解析结束销毁不用的资源
            //注意不要调用datas.clear(),否则getDatas为null
        }

        public List<List<String>> getDatas() {
            return datas;
        }
        public void setDatas(List<List<String>> datas) {
            this.datas = datas;
        }
    }

    /**
     * 模型解析监听器 -- 每解析一行会回调invoke()方法，整个excel解析结束会执行doAfterAllAnalysed()方法
     */
    private static class ModelExcelListener<E> extends AnalysisEventListener<E> {
        private List<E> dataList = new ArrayList<E>();

        @Override
        public void invoke(E object, AnalysisContext context) {
            dataList.add(object);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
        }

        public List<E> getDataList() {
            return dataList;
        }

        @SuppressWarnings("unused")
        public void setDataList(List<E> dataList) {
            this.dataList = dataList;
        }
    }

    /**
     * 使用 模型 来读取Excel
     *
     * @param inputStream Excel的输入流
     * @param clazz 模型的类
     * @param excelTypeEnum Excel的格式(XLS或XLSX)
     *
     * @return 返回 模型 的列表
     */
    public static <E> List<E> readExcelWithModel(InputStream inputStream, Class<? extends BaseRowModel> clazz, ExcelTypeEnum excelTypeEnum) {
        // 解析每行结果在listener中处理
        ModelExcelListener<E> listener = new ModelExcelListener<E>();
        ExcelReader excelReader = new ExcelReader(inputStream, excelTypeEnum, null, listener);
        //默认只有一列表头
        excelReader.read(new Sheet(1, 1, clazz));

        return listener.getDataList();
    }

    /**
     * 使用 StringList 来读取Excel
     * @param inputStream Excel的输入流
     * @param excelTypeEnum Excel的格式(XLS或XLSX)
     * @return 返回 StringList 的列表
     */
    public static List<List<String>> readExcelWithStringList(InputStream inputStream, ExcelTypeEnum excelTypeEnum) {
        StringExcelListener listener = new StringExcelListener();
        ExcelReader excelReader = new ExcelReader(inputStream, excelTypeEnum, null, listener);
        excelReader.read();
        return  listener.getDatas();
    }

    /**
     * 下载文件
     * @param response
     * @param exportFileName 文件名
     * @param head 实体类
     * @param dataList  数据
     * @throws IOException
     */
    public static void exportDefaultExcel(HttpServletResponse response, String exportFileName, Class head, List<?> dataList) throws IOException {
        // 这里注意 使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode(exportFileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
//        EasyExcel.write(response.getOutputStream(), head)
//                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet("Sheet1").doWrite(dataList);
        EasyExcel.write(response.getOutputStream(), head).sheet("Sheet1").doWrite(dataList);
    }

    /**
     * 下载文件
     * @param response
     * @param exportFileName 文件名
     * @param dataList  数据
     * @throws IOException
     */
    public static void exportModelExcel(HttpServletResponse response, String exportFileName, List<?> dataList) throws IOException {
        // 这里注意 使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode(exportFileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), null).sheet("Sheet1").doWrite(dataList);
    }
    /**
     * 自定义动态表头
     * .head(header)这个方法接受一个List<List<>>对象作为动态表头, 该List的结构与excel表头的对应关系：
     *      内层List：每个List对应的是表头中的每一列单元格，长度最大的List的长度决定了表头的行数，并且会合并每个List下标和内容都相同的单元格
     *      外层List：最终的表头结构
     *
     * @param response
     * @param exportFileName 文件名
     * @param dataList  数据
     * @throws IOException
     */
    public static void exportCustomExcel(HttpServletResponse response, String exportFileName, List<?> dataList, List<List<String>> header ) throws IOException {

        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置为红色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 10);
        headWriteCellStyle.setWriteFont(headWriteFont);

        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        contentWriteCellStyle.setFillPatternType(FillPatternType.FINE_DOTS);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        contentWriteCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        contentWriteCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        contentWriteCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        // 背景绿色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short) 10);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(exportFileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream())
                .registerWriteHandler(horizontalCellStyleStrategy)
                // 这里放入动态头
                .head(header).sheet("Sheet1")
                .doWrite(dataList);
    }
    
    //合并一次加打印模板
    public static void exportPrintExcel(HttpServletResponse response, String exportFileName, Class head, List<?> dataList,int[] merge)  throws IOException {
        //参考网址  https://blog.csdn.net/jiqiren1994/article/details/109199377
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置为白色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        
        //垂直居中,水平居中
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        
//        headWriteCellStyle.setBorderBottom(BorderStyle.NONE);
//        headWriteCellStyle.setBorderLeft(BorderStyle.NONE);
//        headWriteCellStyle.setBorderRight(BorderStyle.NONE);
//        headWriteCellStyle.setBorderTop(BorderStyle.NONE);
        
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 10);
        headWriteCellStyle.setWriteFont(headWriteFont);
        
        
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        contentWriteCellStyle.setFillPatternType(FillPatternType.FINE_DOTS);
        //垂直居中,水平居中
      	contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
      	contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        
//        contentWriteCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//        contentWriteCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//        contentWriteCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
//        contentWriteCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        
        // 背景白色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short) 8);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        
        ExcelSheetStrategy sheetStyle=new ExcelSheetStrategy();  //定义sheet样式拦截器，页面打印设置，页眉页脚，页边距
        ExcelMergeStrategy mergeStrategy= new ExcelMergeStrategy(); //定义cell合并拦截器
        if(merge != null ){
        	mergeStrategy.add(merge[0],merge[1],merge[2],merge[3]);
        }
        
        
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
//        String fileName = URLEncoder.encode(exportFileName, "UTF-8");
        String fileName = URLEncoder.encode(exportFileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
//        EasyExcel.write(response.getOutputStream()).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
//                // 这里放入动态头
//                .head(header).sheet("Sheet1")
//                .doWrite(dataList);
        EasyExcel.write(response.getOutputStream(),head)
                .registerWriteHandler(horizontalCellStyleStrategy)
                .registerWriteHandler(mergeStrategy)
                .registerWriteHandler(sheetStyle)
                // 这里放入动态头
                .sheet("Sheet1")
                .doWrite(dataList);
    }
    
  //合并多次模板
    public static void exportMergeExcel(HttpServletResponse response, String exportFileName, Class head, List<?> dataList,ExcelMergeStrategy mergeStrategy)  throws IOException {
        //参考网址  https://blog.csdn.net/jiqiren1994/article/details/109199377
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置为白色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        
        //垂直居中,水平居中
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        
//        headWriteCellStyle.setBorderBottom(BorderStyle.NONE);
//        headWriteCellStyle.setBorderLeft(BorderStyle.NONE);
//        headWriteCellStyle.setBorderRight(BorderStyle.NONE);
//        headWriteCellStyle.setBorderTop(BorderStyle.NONE);
        
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 10);
        headWriteCellStyle.setWriteFont(headWriteFont);
        
        
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        contentWriteCellStyle.setFillPatternType(FillPatternType.FINE_DOTS);
        //垂直居中,水平居中
      	contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
      	contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        
//        contentWriteCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//        contentWriteCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
//        contentWriteCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
//        contentWriteCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        
        // 背景白色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        WriteFont contentWriteFont = new WriteFont();
        
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short) 8);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码
//        String fileName = URLEncoder.encode(exportFileName, "UTF-8");
        String fileName = URLEncoder.encode(exportFileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
//        EasyExcel.write(response.getOutputStream()).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
//                // 这里放入动态头
//                .head(header).sheet("Sheet1")
//                .doWrite(dataList);
        EasyExcel.write(response.getOutputStream(),head)
                .registerWriteHandler(horizontalCellStyleStrategy)
                .registerWriteHandler(mergeStrategy)
                // 这里放入动态头
                .sheet("Sheet1")
                .doWrite(dataList);
    }



}