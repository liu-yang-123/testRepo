package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName BoxpackTaskDTO
 * @Description 早送晚收任务
 * @author 秦江南
 * @Date 2021年11月30日下午4:39:58
 */
@Data
public class BoxpackTaskDTO {
	/**
     * 自增
     */
    private Long id;

    /**
     * 上报机构
     */
    private String bankName;
    
    /**
     * 任务类型
     */
    private Integer taskType;
    
    /**
     * 任务用车
     */
    private String lpno;

    /**
     * 任务日期
     */
    private Long taskDate;

    /**
     * 线路编号
     */
    private String routeNo;

    /**
     * 尾箱列表
     */
    private String boxList;

    /**
     * 任务状态
     */
    private Integer statusT;

    /**
     * 上报时间
     */
    private Long createTime;

}
