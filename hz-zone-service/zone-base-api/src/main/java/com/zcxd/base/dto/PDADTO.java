package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName PdaDTO
 * @Description 终端设备
 * @author 秦江南
 * @Date 2021年5月26日下午7:23:13
 */
@Data
public class PDADTO{

    /**
     * 自增
     */
    private Long id;

    /**
     * 终端编号
     */
    private String tersn;

    /**
     * 物理地址
     */
    private String mac;

    /**
     * 用途(尾箱，清机、库管）
     */
    private Integer useType;

    /**
     * 所属顶级部门
     */
    private Long departmentId;
    
    /**
     * 所属机构
     */
    private Long bankId;
   
    /**
     * 所属机构
     */
    private String bankName;

    /**
     * 备注
     */
    private String comments;

    /**
     * 状态
     */
    private Integer statusT;

}
