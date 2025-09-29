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
public class GunTaskInfoRecordHead {
    @ExcelProperty(value = { "行1标题", "序号" })
    @ColumnWidth(5)
    private Long index;

    @ExcelProperty(value = { "行1标题", "车辆号码" })
    @ColumnWidth(12)
    private String carNo;

    @ExcelProperty(value = { "行1标题", "线路名称" })
    @ColumnWidth(12)
    private String lineName;

    @ExcelProperty(value = { "行1标题", "计划发枪时间" })
    @ColumnWidth(12)
    private String planTime;

    @ExcelProperty(value = { "行1标题", "钥匙员" })
    @ColumnWidth(5)
    private String keyManName;

    @ExcelProperty(value = { "行1标题", "密码员" })
    @ColumnWidth(5)
    private String operatorNameName;

    @ExcelProperty(value = { "行1标题", "司机" })
    @ColumnWidth(5)
    private String dirver;

    @ExcelProperty(value = { "行1标题", "押运员A" })
    @ColumnWidth(5)
    private String supercargoNameA;

    @ExcelProperty(value = { "行1标题", "押运员A工号" })
    @ColumnWidth(5)
    private String supercargoNumA;

    @ExcelProperty(value = { "行1标题", "枪支编号A" })
    @ColumnWidth(5)
    private String gunCodeA;

    @ExcelProperty(value = { "行1标题", "弹盒编号A" })
    @ColumnWidth(5)
    private String gunBoxCodeA;

    @ExcelProperty(value = { "行1标题", "押运员B" })
    @ColumnWidth(5)
    private String supercargoNameB;

    @ExcelProperty(value = { "行1标题", "押运员B工号" })
    @ColumnWidth(5)
    private String supercargoNumB;

    @ExcelProperty(value = { "行1标题", "枪支编号B" })
    @ColumnWidth(5)
    private String gunCodeB;

    @ExcelProperty(value = { "行1标题", "弹盒编号B" })
    @ColumnWidth(5)
    private String gunBoxCodeB;

    @ExcelProperty(value = { "行1标题", "开始时间" })
    @ColumnWidth(15)
    private String taskStartTime;

    @ExcelProperty(value = { "行1标题", "结束时间" })
    @ColumnWidth(15)
    private String taskEndTime;

    @ExcelProperty(value = { "行1标题", "任务状态" })
    @ColumnWidth(5)
    private String taskStatus;

    @ExcelProperty(value = { "行1标题", "持续时间" })
    @ColumnWidth(9)
    private String duration;
}
