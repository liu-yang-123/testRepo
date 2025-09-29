package com.zcxd.base.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songanwei
 * @date 2021/5/12 16:55
 */
@Data
public class DeviceVO implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 所在部门
     */
    private Long departmentId;

    /**
     * 设备编号
     */
    private String deviceNo;

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
     * 购买日期
     */
    private Long enrollDate;

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

}
