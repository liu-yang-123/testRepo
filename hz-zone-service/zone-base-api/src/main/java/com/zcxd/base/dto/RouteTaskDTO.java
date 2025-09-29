package com.zcxd.base.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author songanwei
 * @date 2021-08-10
 */
@Data
public class RouteTaskDTO implements Serializable {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 线路ID
     */
    private Long routeId;

    /**
     * 线路名称
     */
    private String routeName;

    /**
     * 线路备用金
     */
    private BigDecimal cashAmount;

    /**
     * ATM任务列表数据
     */
    private List<AtmTaskDTO> taskList;

}
