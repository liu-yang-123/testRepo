package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName EmployeeVO
 * @Description 员工信息
 * @author 秦江南
 * @Date 2021年5月14日上午11:33:48
 */
@Data
public class EmployeeDTO{

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
     * 年龄
     */
    private Integer age;

    /**
     * 所属部门
     */
    private Long departmentId;

    /**
     * 岗位
     */
    private Long jobIds;
    
    /**
     * 所属部门
     */
    private String departmentName;

    /**
     * 职务
     */
    private Integer title;

    /**
     * 性别 0-男 1-女
     */
    private Integer sex;

    /**
     * 入职日期
     */
    private Long entryDate;
    
    /**
     * 在职状态：0 - 在职，1 - 离职
     */
    private Integer statusT;


}
