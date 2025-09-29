package com.zcxd.pda.dto;

import lombok.Data;

/**
 * @author songanwei
 * @date 2021-12-01
 */
@Data
public class BoxpackTaskDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * 用户类型 0-库房 1-柜员
     */
    private Integer userType;

    /**
     * 任务类型
     */
    private Integer taskType;
    /**
     * 任务描述
     */
    private String taskText;
    /**
     * 线路编号
     */
    private String routeNo;
    /**
     * 车牌编号
     */
    private String vehicleNo;
    /**
     * 数量
     */
    private Integer number;
    /**
     * 申报时间
     */
    private Long createTime;

    /**
     * 状态
     */
    private Integer statusT;

}
