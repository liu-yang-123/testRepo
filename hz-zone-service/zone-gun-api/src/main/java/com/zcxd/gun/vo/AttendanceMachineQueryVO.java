package com.zcxd.gun.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zccc
 */
@Data
public class AttendanceMachineQueryVO {
    /**
     * ip
     */
    private String address;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 机器号
     */
    private Integer machineNum;
    /**
     * 状态
     * 0：不可用
     * 1：可用
     */
    private Short status;
    /**
     * 部门
     */
    @NotNull
    private Integer departmentId;
}
