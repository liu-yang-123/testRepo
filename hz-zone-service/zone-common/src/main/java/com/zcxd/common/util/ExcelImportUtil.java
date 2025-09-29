package com.zcxd.common.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * @ClassName ExcelUtil
 * @Description excel导入工具类
 * @author 秦江南
 * @Date 2021年7月6日下午7:30:30
 */
public class ExcelImportUtil {
	/**
     * 
     * @Title readExcel
     * @Description 读取excel
     * @param file
     * @return
     * @return 返回类型 Workbook
     */
    public static Workbook readExcel(InputStream is, String extString){
        Workbook wb = null;
//        if(file == null){
//            return null;
//        }
//        String extString = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
//        InputStream is = null;
        try {
//            is = file.getInputStream();
            if(".xls".equals(extString)){
                return wb = new HSSFWorkbook(is);
            }else if(".xlsx".equals(extString)){
                return wb = new XSSFWorkbook(is);
            }else{
                return wb = null;
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }
    
    /**
     * 
     * @Title getCellFormatValue
     * @Description 格式化单元格数据
     * @param cell
     * @return
     * @return 返回类型 Object
     */
    public static Object getCellFormatValue(Cell cell){
        Object cellValue = null;
        if(cell!=null){
            //判断cell类型
            switch(cell.getCellType()){
	            case Cell.CELL_TYPE_NUMERIC:{
	                cellValue = String.valueOf(cell.getNumericCellValue());
	                break;
	            }
	            case Cell.CELL_TYPE_FORMULA:{
	                //判断cell是否为日期格式
	                if(DateUtil.isCellDateFormatted(cell)){
	                    //转换为日期格式YYYY-mm-dd
	                    cellValue = cell.getDateCellValue();
	                }else{
	                    //数字
	                    cellValue = String.valueOf(cell.getNumericCellValue());
	                }
	                break;
	            }
	            case Cell.CELL_TYPE_STRING:{
	                cellValue = cell.getRichStringCellValue().getString();
	                break;
	            }
	            default:
	                cellValue = "";
            }
        }else{
            cellValue = "";
        }
        return cellValue;
    }
    
    //判断字符串是否为数字或小数
    public static boolean checkNum(String str) {
    	Pattern pattern = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }else{
        	return true;
        }

	}
    
}
