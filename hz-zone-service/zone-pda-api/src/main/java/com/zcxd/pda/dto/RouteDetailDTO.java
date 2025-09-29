package com.zcxd.pda.dto;

import lombok.Data;

import java.util.List;

/**
 * 线路详情DTO
 */
@Data
public class RouteDetailDTO {
    private Long id;
    /**
     * 线路编号
     */
    private String routeNo;

    /**
     * 线路类型
     */
    private String routeType;

    /**
     * 分配车辆车牌
     */
    private String vehicleLpNo;

    /**
     * 任务日期
     */
    private Long routeDate;

    /**
     * 司机
     */
    private Long driver;
    private String driverName;

    /**
     * 护卫A
     */
    private Long securityA;
    private String securityAName;

    /**
     * 护卫B
     */
    private Long securityB;
    private String securityBName;

    /**
     * 业务-钥匙员
     */
    private Long routeKeyMan;
    private String routeKeyManName;
    /**
     * 业务-清机员
     */
    private Long routeOperMan;
    private String routeOperManName;
    /**
     * 银行子线路信息
     */
    private List<SubRouteBankDenomDTO> subRouteBankDenomDTOS;
    /**
     * 钞盒列表
     */
    private String dispBoxList;
    /**
     * 钞盒数量
     */
    private Integer dispBoxCount;
    /**
     * 未使用钞盒数量
     */
    private Integer notUsedBoxCount;
    
    /**
     * 未使用钞盒
     */
    private List<String> notUsedBoxNos;

    /**
     * 钞袋数量
     */
    private Integer dispBagCount;

    /**
     * 线路状态
     */
    private Integer statusT;

    /**
     * 总任务数
     */
    private Integer taskTotal;

    /**
     * 车长日志上传标志
     */
    private Integer leaderLog;
}
