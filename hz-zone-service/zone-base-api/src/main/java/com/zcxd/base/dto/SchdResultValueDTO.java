package com.zcxd.base.dto;

import lombok.Data;

/**
 * @author songanwei
 * @date 2021-07-24
 */
@Data
public class SchdResultValueDTO {

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 是否车长
     */
    private Boolean isLeader;

    /**
     * 线路组别
     */
    private Long routeId;

    /**
     * 线路编号
     */
    private String routeNo;

    /**
     * 线路类型
     */
    private Integer routeType;

    /**
     * 车牌号码
     */
    private String vehicleNo;

    /**
     * 司机
     */
    private Long driver;

    /**
     * 护卫1
     */
    private Long scurityA;

    /**
     * 护卫2
     */
    private Long scurityB;

    /**
     * 钥匙员
     */
    private Long keyMan;

    /**
     * 密码操作员
     */
    private Long opMan;

    /**
     * 车长
     */
    private Long leader;
}
