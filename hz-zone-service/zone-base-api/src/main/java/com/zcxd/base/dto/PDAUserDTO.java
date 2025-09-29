package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName PDAUserDTO
 * @Description PDA用户信息
 * @author 秦江南
 * @Date 2021年5月25日下午7:17:28
 */
@Data
public class PDAUserDTO{

    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 员工工号
     */
    private String empNo;

    /**
     * 员工姓名
     */
    private String empName;
    
    /**
     * 所属部门
     */
    private String departmentName;

    /**
     * 岗位
     */
    private String jobName;

    /**
     * 职务
     */
    private Integer title;
    
//    /**
//     * PDA角色
//     */
//    private Long roleId;
    
    private Integer pdaEnable;
    private Integer pdaAdmin;

}
