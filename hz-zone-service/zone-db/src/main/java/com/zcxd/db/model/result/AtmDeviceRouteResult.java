package com.zcxd.db.model.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songanwei
 * @date 2021-10-20
 */
@Data
public class AtmDeviceRouteResult implements Serializable {

    /**
     * 设备ID
     */
    private Long id;

    /**
     * 设备编号
     */
    private String terNo;

    /**
     * 线路编号
     */
    private String routeNo;

}
