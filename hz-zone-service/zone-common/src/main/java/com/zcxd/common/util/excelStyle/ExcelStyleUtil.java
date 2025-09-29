package com.zcxd.common.util.excelStyle;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;

public class ExcelStyleUtil {

    private ExcelStyleUtil() {}

    /**
     * @param workbook
     * @return
     */
    public static CellStyle buildDefaultCellStyle(Workbook workbook) {
        CellStyle newCellStyle = workbook.createCellStyle();
        newCellStyle.setWrapText(true);
        newCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        newCellStyle.setAlignment(HorizontalAlignment.CENTER);
        newCellStyle.setLocked(true);
        newCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        newCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        newCellStyle.setBorderTop(BorderStyle.THIN);
        newCellStyle.setBorderBottom(BorderStyle.THIN);
        newCellStyle.setBorderLeft(BorderStyle.THIN);
        newCellStyle.setBorderRight(BorderStyle.THIN);
        return newCellStyle;
    }

    /**
     * Build head cell style
     *
     * @param workbook
     * @param writeCellStyle
     * @return
     */
    public static CellStyle buildHeadCellStyle(Workbook workbook, WriteCellStyle writeCellStyle) {
        CellStyle cellStyle = buildDefaultCellStyle(workbook);
        if (writeCellStyle == null) {
            return cellStyle;
        }
        buildCellStyle(workbook, cellStyle, writeCellStyle, true);
        return cellStyle;
    }

    /**
     * Build content cell style
     *
     * @param workbook
     * @param writeCellStyle
     * @return
     */
    public static CellStyle buildContentCellStyle(Workbook workbook, WriteCellStyle writeCellStyle) {
        CellStyle cellStyle = workbook.createCellStyle();
        if (writeCellStyle == null) {
            return cellStyle;
        }
        buildCellStyle(workbook, cellStyle, writeCellStyle, false);
        return cellStyle;
    }
    public static void buildCellStyle(Workbook workbook,Cell cell,CellStyle cellStyle, ExcelStyle excelStyle) {
        buildCellStyle(workbook, cellStyle, excelStyle.getStyle(), false);
    }

    public static void buildCellStyle(Workbook workbook, CellStyle cellStyle, WriteCellStyle writeCellStyle,
                                       boolean isHead) {
        buildFont(workbook, cellStyle, writeCellStyle.getWriteFont(), isHead);
        if (writeCellStyle.getDataFormat() != null) {
            cellStyle.setDataFormat(writeCellStyle.getDataFormat());
        }
        if (writeCellStyle.getHidden() != null) {
            cellStyle.setHidden(writeCellStyle.getHidden());
        }
        if (writeCellStyle.getLocked() != null) {
            cellStyle.setLocked(writeCellStyle.getLocked());
        }
        if (writeCellStyle.getQuotePrefix() != null) {
            cellStyle.setQuotePrefixed(writeCellStyle.getQuotePrefix());
        }
        if (writeCellStyle.getHorizontalAlignment() != null) {
            cellStyle.setAlignment(writeCellStyle.getHorizontalAlignment());
        }
        if (writeCellStyle.getWrapped() != null) {
            cellStyle.setWrapText(writeCellStyle.getWrapped());
        }
        if (writeCellStyle.getVerticalAlignment() != null) {
            cellStyle.setVerticalAlignment(writeCellStyle.getVerticalAlignment());
        }
        if (writeCellStyle.getRotation() != null) {
            cellStyle.setRotation(writeCellStyle.getRotation());
        }
        if (writeCellStyle.getIndent() != null) {
            cellStyle.setIndention(writeCellStyle.getIndent());
        }
        if (writeCellStyle.getBorderLeft() != null) {
            cellStyle.setBorderLeft(writeCellStyle.getBorderLeft());
        }
        if (writeCellStyle.getBorderRight() != null) {
            cellStyle.setBorderRight(writeCellStyle.getBorderRight());
        }
        if (writeCellStyle.getBorderTop() != null) {
            cellStyle.setBorderTop(writeCellStyle.getBorderTop());
        }
        if (writeCellStyle.getBorderBottom() != null) {
            cellStyle.setBorderBottom(writeCellStyle.getBorderBottom());
        }
        if (writeCellStyle.getLeftBorderColor() != null) {
            cellStyle.setLeftBorderColor(writeCellStyle.getLeftBorderColor());
        }
        if (writeCellStyle.getRightBorderColor() != null) {
            cellStyle.setRightBorderColor(writeCellStyle.getRightBorderColor());
        }
        if (writeCellStyle.getTopBorderColor() != null) {
            cellStyle.setTopBorderColor(writeCellStyle.getTopBorderColor());
        }
        if (writeCellStyle.getBottomBorderColor() != null) {
            cellStyle.setBottomBorderColor(writeCellStyle.getBottomBorderColor());
        }
        if (writeCellStyle.getFillPatternType() != null) {
            cellStyle.setFillPattern(writeCellStyle.getFillPatternType());
        }
        if (writeCellStyle.getFillBackgroundColor() != null) {
            cellStyle.setFillBackgroundColor(writeCellStyle.getFillBackgroundColor());
        }
        if (writeCellStyle.getFillForegroundColor() != null) {
            cellStyle.setFillForegroundColor(writeCellStyle.getFillForegroundColor());
        }
        if (writeCellStyle.getShrinkToFit() != null) {
            cellStyle.setShrinkToFit(writeCellStyle.getShrinkToFit());
        }
    }

    private static void buildFont(Workbook workbook, CellStyle cellStyle, WriteFont writeFont, boolean isHead) {
        Font font = null;
        if (isHead) {
            font = workbook.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short)14);
            font.setBold(true);
            cellStyle.setFont(font);
        }
        if (writeFont == null) {
            return;
        }
        if (!isHead) {
            font = workbook.createFont();
            cellStyle.setFont(font);
        }
        if (writeFont.getFontName() != null) {
            font.setFontName(writeFont.getFontName());
        }
        if (writeFont.getFontHeightInPoints() != null) {
            font.setFontHeightInPoints(writeFont.getFontHeightInPoints());
        }
        if (writeFont.getItalic() != null) {
            font.setItalic(writeFont.getItalic());
        }
        if (writeFont.getStrikeout() != null) {
            font.setStrikeout(writeFont.getStrikeout());
        }
        if (writeFont.getColor() != null) {
            font.setColor(writeFont.getColor());
        }
        if (writeFont.getTypeOffset() != null) {
            font.setTypeOffset(writeFont.getTypeOffset());
        }
        if (writeFont.getUnderline() != null) {
            font.setUnderline(writeFont.getUnderline());
        }
        if (writeFont.getCharset() != null) {
            font.setCharSet(writeFont.getCharset());
        }
        if (writeFont.getBold() != null) {
            font.setBold(writeFont.getBold());
        }
    }

    public static void buildBorderStyle(Sheet sheet, CellRangeAddress address, ExcelBorderStyle style) {
        if ( style.getBorderBottom() != null) {
            RegionUtil.setBorderBottom(style.getBorderBottom(),address, sheet);
        }
        if ( style.getBorderLeft() != null) {
            RegionUtil.setBorderLeft(style.getBorderLeft(),address, sheet);
        }
        if ( style.getBorderRight() != null) {
            RegionUtil.setBorderRight(style.getBorderRight(),address, sheet);
        }
        if ( style.getBorderTop() != null) {
            RegionUtil.setBorderTop(style.getBorderTop(),address, sheet);
        }
        if(style.getColorBottom()!= null){
            RegionUtil.setBottomBorderColor(style.getColorBottom(),address, sheet);
        }
        if(style.getColorLeft()!= null){
            RegionUtil.setLeftBorderColor(style.getColorLeft(),address, sheet);
        }
        if(style.getColorRight()!= null){
            RegionUtil.setRightBorderColor(style.getColorRight(),address, sheet);
        }
        if(style.getColorTop()!= null){
            RegionUtil.setTopBorderColor(style.getColorTop(),address, sheet);
        }
    }

    public static WriteCellStyle GenericStyle(){
        WriteCellStyle style= new WriteCellStyle();
        style.setHorizontalAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        style.setWrapped(true);
        WriteFont font = new WriteFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 11);
        style.setWriteFont(font);

        return style;
    }

    public static WriteCellStyle GenericBorderStyle(){
        WriteCellStyle style= new WriteCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    public static WriteCellStyle RightMediumBorderStyle(){
        WriteCellStyle style= new WriteCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.MEDIUM);
        return style;
    }

    public static WriteCellStyle LeftMediumBorderStyle(){
        WriteCellStyle style= new WriteCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    public static WriteCellStyle BottomMediumBorderStyle(){
        WriteCellStyle style= new WriteCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    public static WriteCellStyle LeftBottomMediumBorderStyle(){
        WriteCellStyle style= new WriteCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    public static WriteCellStyle RightBottomMediumBorderStyle(){
        WriteCellStyle style= new WriteCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.MEDIUM);
        return style;
    }

    public static WriteCellStyle TitleStyle(){
        WriteCellStyle style= new WriteCellStyle();
        WriteFont font = new WriteFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 18);
        style.setWriteFont(font);
        return style;
    }
}