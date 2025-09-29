package com.zcxd.base.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceDTO implements Serializable {

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 归属部门
     */
    private Long departmentId;

    /**
     * 归属部门名称
     */
    private String departmentName;

    /**
     * 设备编号
     */
    private String deviceNo;

    /**
     * 设备型号
     */
    private String modelName;

    /**
     * 品牌名称
     */
    private String factoryName;

    /**
     * 设备型号
     */
    private Long modelId;

    /**
     * 设备厂商
     */
    private Long factoryId;

    /**
     * 设备出厂SN
     */
    private String deviceSn;

    /**
     * 设备所在位置
     */
    private String location;

    /**
     * 设备ip地址
     */
    private String ipaddr;

    /**
     * 设备状态 0- 正常使用，1 - 维修中，2 - 报废
     */
    private String statusT;

    /**
     * 备注
     */
    private String comments;

    /**
     * 是否已分配 0 - 未分配，1 - 已分配
     */
    private Integer assigned;

    /**
     * 购买日期
     */
    private Long enrollDate;

}
