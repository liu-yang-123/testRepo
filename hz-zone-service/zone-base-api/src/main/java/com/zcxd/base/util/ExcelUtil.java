package com.zcxd.base.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * Excel工具类
 */
public class ExcelUtil {

    /**
     * 获取ExcelWriter实例对象
     * @param response
     * @return
     * @throws IOException
     */
    public ExcelWriter getExcelWriter(String title, HttpServletResponse response) throws IOException {
        ExcelWriter excelWriter = null;
        String fileName = URLEncoder.encode(title, "UTF-8").replaceAll("\\+", "%20");
        //写入excel
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        //写入到流
        excelWriter = EasyExcel.write(response.getOutputStream()).build();

        return excelWriter;
    }

    /**
     *
     * @param title 标题
     * @param inputStream   模板文件流
     * @param response 响应对象
     * @return
     * @throws IOException
     */
    public ExcelWriter getTemplateWriter(String title, InputStream inputStream, HttpServletResponse response) throws IOException{
        ExcelWriter excelWriter = null;
        String fileName = URLEncoder.encode(title, "UTF-8").replaceAll("\\+", "%20");
        //写入excel
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        //写入到流
        excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(inputStream).build();

        return excelWriter;
    }
}
