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
public class GunInfoRecordHead {
    @ExcelProperty(value = { "行1标题", "序号" })
    @ColumnWidth(5)
    private Long index;

    @ExcelProperty(value = { "行1标题", "枪号" })
    @ColumnWidth(12)
    private String gunCode;

    @ExcelProperty(value = { "行1标题", "枪证号" })
    @ColumnWidth(12)
    private String gunLicenceNum;

    @ExcelProperty(value = { "行1标题", "内部编号" })
    @ColumnWidth(9)
    private Long internalNum;

    @ExcelProperty(value = { "行1标题", "配发日期" })
    @ColumnWidth(9)
    private String buyDate;

    @ExcelProperty(value = { "行1标题", "类型" })
    @ColumnWidth(5)
    private String gunType;

    @ExcelProperty(value = { "行1标题", "枪支状态" })
    @ColumnWidth(9)
    private String gunStatus;

    @ExcelProperty(value = { "行1标题", "出勤次数" })
    @ColumnWidth(9)
    private Integer userCount;

    @ExcelProperty(value = { "行1标题", "备注" })
    @ColumnWidth(40)
    private String remark;

}
