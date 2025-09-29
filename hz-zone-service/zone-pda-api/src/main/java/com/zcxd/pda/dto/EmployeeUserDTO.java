package com.zcxd.pda.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author shijin
 * @date 2021/5/26 10:17
 */
@ApiModel("员工用户列表")
@Data
public class EmployeeUserDTO implements Serializable {
    /**
     * 用户id
     */
    private Long id;
    /**
     * 工号
     */
    private String empNo;
    /**
     * 姓名
     */
    private String empName;

    /**
     * 岗位
     */
    private String jobName;

    /**
     * 部门
     */
    private String departmentName;

    /**
     * 是否已录指纹 1=是 0=否
     */
    private Integer isFingerPrint;

    /**
     * 手机号
     */
    private String mobile;
    
    /**
     * 是否主管
     */
    private Integer master;

    /**
     * 是否管理员
     */
    private Integer managerFlag;

    /**
     * 备注
     */
    private String comment;

}
