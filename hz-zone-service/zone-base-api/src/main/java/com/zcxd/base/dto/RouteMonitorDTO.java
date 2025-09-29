package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName RouteMonitorDTO
 * @Description 线路监控
 * @author 秦江南
 * @Date 2021年6月21日下午4:57:38
 */
@Data
public class RouteMonitorDTO {

    /**
     * 唯一标识
     */
    private Long id;

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
     * 车牌编号
     */
    private String seqno;
    
    /**
     * 车牌号码
     */
    private String lpno;
    

    /**
     * 实际开始时间
     */
    private Long actBeginTime;

    /**
     * 实际结束时间
     */
    private Long actFinishTime;

    /**
     * 线路状态
     */
    private Integer statusT;

    /**
     * 总任务数
     */
    private Integer taskTotal;

    /**
     * 完成任务数
     */
    private Integer taskFinish;

    /**
     * 备注
     */
    private String comments;


}
