package com.zcxd.base.dto.report;

import lombok.Data;
import java.io.Serializable;

/**
 * @author songanwei
 * @date 2021-10-21
 **/
@Data
public class RouteTimeReportDTO implements Serializable {

    /**
     * 线路
     */
    private String routeNo;
    /**
     * 出车次数
     */
    private Long times;

    /**
     * 总任务数
     */
    private Long total;

    /**
     * 平均时长（小时）
     */
    private Double average;

    /**
     * 最早（小时）
     */
    private Double earliest;

    /**
     * 最晚（小时）
     */
    private Double latest;

    /**
     * 平均每台用时（分钟）
     */
    private Double averagePer;
}
