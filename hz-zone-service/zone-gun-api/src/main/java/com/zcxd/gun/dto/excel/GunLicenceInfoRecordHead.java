package com.zcxd.gun.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * @author zccc
 */
@Data
@ContentRowHeight(20)
@HeadRowHeight(25)
public class GunLicenceInfoRecordHead {
    @ExcelProperty(value = { "行1标题", "序号" })
    @ColumnWidth(5)
    private Long index;

    @ExcelProperty(value = { "行1标题", "员工姓名" })
    @ColumnWidth(8)
    private String empName;

    @ExcelProperty(value = { "行1标题", "员工工号" })
    @ColumnWidth(12)
    private String empNo;

    @ExcelProperty(value = { "行1标题", "保安证号" })
    @ColumnWidth(12)
    private String securityNum;

    @ExcelProperty(value = { "行1标题", "持枪证" })
    @ColumnWidth(12)
    private String gunLicenceNum;

    @ExcelProperty(value = { "行1标题", "持枪证有效期" })
    @ColumnWidth(9)
    private String gunLicenceValidity;

    @ExcelProperty(value = { "行1标题", "证件管理状态" })
    @ColumnWidth(15)
    private String licenceStatus;
}
