package com.zcxd.gun.dto;

import lombok.Data;

/**
 * @author zccc
 */
@Data
public class GunConfigDTO {
    private Long id;
    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 护卫1
     */
    private Long scurityA;

    /**
     * 护卫2
     */
    private Long scurityB;

    /**
     * 线路编号
     */
    private String routeNo;

    /**
     * 车牌号码
     */
    private String vehicleNo;
}
