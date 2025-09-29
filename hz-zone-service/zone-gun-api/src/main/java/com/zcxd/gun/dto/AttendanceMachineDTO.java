package com.zcxd.gun.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author zccc
 */
@Data
public class AttendanceMachineDTO {
    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
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
}
