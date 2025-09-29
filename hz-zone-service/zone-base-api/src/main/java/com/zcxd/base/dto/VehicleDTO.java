package com.zcxd.base.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName VehicleDTO
 * @Description 车辆信息
 * @author 秦江南
 * @Date 2021年5月19日下午3:42:20
 */
@Data
public class VehicleDTO {

    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 车牌号码
     */
    private String lpno;

    /**
     * 车辆编号
     */
    private String seqno;
    
    /**
     * 所属部门
     */
    private Long departmentId;

    /**
     * 制造商
     */
    private String factory;

    /**
     * 型号
     */
    private String model;

    /**
     * 车辆种类
     */
    private Integer vhType;

    /**
     * 车辆状态
     */
    private Integer statusT;

    /**
     * 购买日期
     */
    private Long enrollDate;
    
    /**
     * 车辆类型
     */
    private String vehicleType;
    
    /**
     * 车架号
     */
    private String frameNumber;
    
    /**
     * 发动机号
     */
    private String engineNumber;
    
    /**
     * 排放标准
     */
    private String emissionStandard;
    
    /**
     * 出厂日期
     */
    private Long productionDate;

    /**
     * 备注
     */
    private String comments;

}
