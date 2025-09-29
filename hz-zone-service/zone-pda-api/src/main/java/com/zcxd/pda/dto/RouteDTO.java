package com.zcxd.pda.dto;

import lombok.Data;


/**
 * 银行库存统计DTO
 */
@Data
public class RouteDTO {
    private Long id; //
    private String routeNo;   //线路编号
    private String routeName; //线路名称
    private String vehicleNo; //车牌号码
    private String driverName; //司机名
    private String securityAName; //护卫名称
    private String securityBName; //护卫名称
    private String keyManName; //钥匙员姓名
    private String operManName; //操作员姓名
    private Integer taskTotal; //总台数
    private Integer unfinishTotal; //未完成任务数
    private Integer allowHandover; //是否允许交接
    private Long routeDate; //线路日期
    private Integer statusT; //状态
    private Integer leaderLog; //状态
    private String comments;
}
