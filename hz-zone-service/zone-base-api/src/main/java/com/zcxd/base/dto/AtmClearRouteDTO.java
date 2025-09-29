package com.zcxd.base.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-06-17
 */
@Data
public class AtmClearRouteDTO implements Serializable {

    /**
     * 线路ID
     */
    private Long routeId;
    /**
     * 任务日期
     */
    private String taskDate;

    /**
     * 线路编号
     */
    private String routeNo;

    /**
     * 线路名称
     */
    private String routeName;

    /**
     * 线路类型
     */
    private Integer routeType;

    /**
     * 计划清分
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal planAmount;

    /**
     * 实际清分
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal clearAmount;

    /**
     * 总任务数
     */
    private Integer totalTask;

    /**
     * 已完成任务数
     */
    private Integer doneTask;
}
