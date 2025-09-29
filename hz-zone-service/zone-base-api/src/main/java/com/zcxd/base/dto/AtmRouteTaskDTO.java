package com.zcxd.base.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date  2021-08-11
 */
@Data
public class AtmRouteTaskDTO implements Serializable {

    /**
     * 主键ID
     */
    private String id;

    /**
     * ATM ID
     */
    private Long atmId;

    /**
     * 线路ID
     */
    private Long routeId;

    /**
     * 线路名称
     */
    private String routeName;

    /**
     * 配钞金额
     */
    private BigDecimal amount;

    /**
     * 券别ID
     */
    private Long denomId;


    /**
     * 设备编号
     */
    private String terNo;

    /**
     * 是否已选择  1=是 0-否
     */
    private Integer isSelected;

}
