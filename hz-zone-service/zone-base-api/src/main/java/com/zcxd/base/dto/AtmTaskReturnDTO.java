package com.zcxd.base.dto;

import lombok.Data;

/**
 * 清机回笼信息
 * @ClassName AtmTaskReturnDTO
 * @Description TODO
 * @author 秦江南
 * @Date 2023年2月13日下午5:03:52
 */
@Data
public class AtmTaskReturnDTO {

    /**
     * 自增
     */
    private Long id;
    
    /**
     * 钞盒,钞袋编号
     */
    private String boxBarCode;
    
    /**
     * 装运方式（钞袋, 钞盒)
     */
    private String carryType;
    
    /**
     * 任务日期
     */
    private String taskDate;
    
    /**
     * 线路名称
     */
    private String routeName;
    
    /**
     * 网点名称
     */
    private String bankName;

    /**
     * 设备编号
     */
    private String terNo;

}
