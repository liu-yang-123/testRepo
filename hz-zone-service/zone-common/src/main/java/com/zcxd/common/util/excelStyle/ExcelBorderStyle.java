package com.zcxd.common.util.excelStyle;

import org.apache.poi.ss.usermodel.BorderStyle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelBorderStyle {
    BorderStyle BorderRight;
    BorderStyle BorderBottom;
    BorderStyle BorderLeft;
    BorderStyle BorderTop;
    Integer ColorRight;
    Integer ColorBottom;
    Integer ColorLeft;
    Integer ColorTop;


    public static ExcelBorderStyle LeftRightMediumBorder(){
        ExcelBorderStyle style= new ExcelBorderStyle();
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }
    public static ExcelBorderStyle RightMediumBorder(){
        ExcelBorderStyle style= new ExcelBorderStyle();
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }

    public static ExcelBorderStyle TopRightLeftMediumBorder(){
        ExcelBorderStyle style= new ExcelBorderStyle();
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    public static ExcelBorderStyle TopRightMediumBorder(){
        ExcelBorderStyle style= new ExcelBorderStyle();
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    public static ExcelBorderStyle BottomRightMediumBorder(){
        ExcelBorderStyle style= new ExcelBorderStyle();
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.MEDIUM);
        return style;
    }
    public static ExcelBorderStyle BottomMediumBorder(){
        ExcelBorderStyle style= new ExcelBorderStyle();
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.MEDIUM);
        return style;
    }

    public static ExcelBorderStyle LeftMediumBorder(){
        ExcelBorderStyle style= new ExcelBorderStyle();
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }
    public static ExcelBorderStyle NormalBorder(){
        ExcelBorderStyle style= new ExcelBorderStyle();
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }
}