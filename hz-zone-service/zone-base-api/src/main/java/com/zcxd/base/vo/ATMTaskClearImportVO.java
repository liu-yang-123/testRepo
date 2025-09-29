package com.zcxd.base.vo;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 
 * @ClassName ATMTaskClearImportVO
 * @Description 导入清分任务
 * @author 秦江南
 * @Date 2021年8月5日下午5:35:52
 */
@ApiModel("导入清分任务")
@Data
public class ATMTaskClearImportVO {
	/**
     * 计划清分金额/库存金额
     */
    private BigDecimal planAmount;

    /**
     * 所属部门ID
     */
    private Long departmentId;

    /**
     * 线路
     */
    private Long routeId;

    /**
     * atm设备
     */
    private Long atmId;
    
    /**
     * 设备编号
     */
    private String terNo;
    
    /**
     * 加钞券别
     */
    private Long denomId;
    
    /**
     * 备注
     */
    private String comments;

    /**
     * 银行网点
     */
    private Long bankId;

}