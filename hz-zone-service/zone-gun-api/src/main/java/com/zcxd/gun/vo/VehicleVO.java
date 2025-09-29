package com.zcxd.gun.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 
 * @ClassName VehicleVO
 * @Description 车辆信息
 * @author 秦江南
 * @Date 2021年5月19日下午3:16:52
 */
@Data
public class VehicleVO {

    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 车牌号码
     */
    @NotBlank(message = "车牌号码不能为空")
    @Size(max=32,message="车牌号码最大长度为32")
    private String lpno;

    /**
     * 车辆编号
     */
    @NotBlank(message = "车辆编号不能为空")
    @Size(max=32,message="车辆编号最大长度为32")
    private String seqno;
    
    /**
     * 所属顶级部门
     */
    @NotNull(message = "所属顶级部门不能为空")  
    private Long departmentId;

    /**
     * 制造商
     */
    @NotBlank(message = "制造商不能为空")
    @Size(max=32,message="制造商最大长度为32")
    private String factory;

    /**
     * 型号
     */
    @NotBlank(message = "型号不能为空")
    @Size(max=32,message="型号最大长度为32")
    private String model;

    /**
     * 车辆种类
     */
    @NotNull(message = "车辆种类不能为空")
    private Integer vhType;

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
    @Size(max=32,message="备注最大长度为32")
    private String comments;

}
