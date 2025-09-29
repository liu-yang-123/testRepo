package com.zcxd.gun.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zccc
 */
@Data
public class AttendanceMachineVO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * ip
     */
    @NotBlank
    private String address;
    /**
     * 端口
     */
    @NotNull
    private Integer port;
    /**
     * 机器号
     */
    @NotNull
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
