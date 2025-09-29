package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName RouteTemplateDTO
 * @Description 线路模板
 * @author 秦江南
 * @Date 2021年5月28日下午2:53:36
 */
@Data
public class RouteTemplateDTO {
    /**
     * 唯一标识
     */
    private Long id;
    
    /**
     * 所属部门
     */
    private Long departmentId;
    
    /**
     * 线路编号
     */
    private String routeNo;

//    /**
//     * ATM设备列表
//     */
//    private String atmList;
//
//    /**
//     * 网点列表
//     */
//    private String bankList;
    
    /**
     * 线路名称
     */
    private String routeName;
    
    
    /**
     * 计划开始时间
     */
    private String planBeginTime;

    /**
     * 计划结束时间
     */
    private String planFinishTime;
    
    /**
     * 线路类型
     */
    private Integer routeType;
    
    /**
     * 线路生成规则
     */
    private Integer rule;
    
    /**
     * 线路生成标志
     */
    private Integer sign;
    
    /**
     * 线路排序
     */
    private Integer sort;
    
    /**
     * 备注
     */
    private String comments;
}
