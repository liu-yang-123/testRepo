package com.zcxd.base.dto;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("线路排班Excel下载信息")
@ContentRowHeight(25)
@Data
public class SchdResultExcelDTO {

    /**
     * 计划日期
     */
    @ColumnWidth(15)
    @ExcelProperty("日期")
    private String planDays;

    /**
     * 线路组别
     */
    @ColumnWidth(10)
    @ExcelProperty("线路")
    private String routeNo;


    /**
     * 车牌号码
     */
    @ColumnWidth(15)
    @ExcelProperty("车辆")
    private String vehicleNo;

    /**
     * 司机名称
     */
    @ColumnWidth(14)
    @ExcelProperty("司机")
    private String driverName;

    /**
     * 护卫1名称
     */
    @ColumnWidth(14)
    @ExcelProperty("护卫1")
    private String scurityAName;

    /**
     * 护卫2名称
     */
    @ColumnWidth(14)
    @ExcelProperty("护卫2")
    private String scurityBName;

    /**
     * 密码操作员名称
     */
    @ColumnWidth(22)
    @ExcelProperty("密码员")
    private String opManName;

    /**
     * 钥匙员名称
     */
    @ColumnWidth(22)
    @ExcelProperty("钥匙员")
    private String keyManName;

    /**
     * 车长名称
     */
    @ColumnWidth(14)
    @ExcelProperty("代理车长")
    private String leaderName;
}