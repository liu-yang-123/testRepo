package com.zcxd.base.handler;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import java.util.List;

/**
 * excel单元格合并处理
 * @author songanwei
 * @date 2021-10-13
 */
public class ExcelMergeHandler extends AbstractMergeStrategy {

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        if (relativeRowIndex == null || relativeRowIndex == 0){
            return;
        }
        int rowIndex = cell.getRowIndex();
        int colIndex = cell.getColumnIndex();
        sheet = cell.getSheet();
        Row preRow = sheet.getRow(rowIndex - 1);
        Cell preCell = preRow.getCell(colIndex);
        List<CellRangeAddress> list = sheet.getMergedRegions();
        CellStyle cs = cell.getCellStyle();
        cell.setCellStyle(cs);
        for (int i = 0; i < list.size(); i++){
            CellRangeAddress cellRangeAddress = list.get(i);
            if (cellRangeAddress.containsRow(preCell.getRowIndex()) && cellRangeAddress.containsColumn(preCell.getColumnIndex())){
                int lastColIndex = cellRangeAddress.getLastColumn();
                int firstColIndex = cellRangeAddress.getFirstColumn();
                CellRangeAddress cra = new CellRangeAddress(cell.getRowIndex(),cell.getRowIndex(),firstColIndex,lastColIndex);
                sheet.addMergedRegion(cra);
                RegionUtil.setBorderBottom(BorderStyle.THIN,cra,sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN,cra,sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN,cra,sheet);
                RegionUtil.setBorderTop(BorderStyle.THIN,cra,sheet);
                return;
            }
        }
    }
}
