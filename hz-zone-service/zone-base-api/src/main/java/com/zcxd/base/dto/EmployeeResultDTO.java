package com.zcxd.base.dto;

import lombok.Data;

/**
 * @author songanwei
 * @date 2021-07-24
 */
@Data
public class EmployeeResultDTO {

    /**
     * 顶级部门ID
     */
    private Long departmentId;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 岗位类型 1=司机岗  2=护卫岗  3=钥匙岗  4=密码岗
     */
    private Integer type;

    /**
     * 是否存在 true  一定存在  false 不一定存在
     */
    private Boolean exist;

    /**
     * 线路车长【针对清机岗、密码岗】
     */
    private Integer routeLeader;

}
