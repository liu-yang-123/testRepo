package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName RouteDTO
 * @Description 线路信息
 * @author 秦江南
 * @Date 2021年5月28日下午5:24:45
 */
@Data
public class RouteDTO {

    /**
     * 唯一标识
     */
    private Long id;
    
    /**
     * 所属部门
     */
    private Long departmentId;

    /**
     * 线路编号
     */
    private String routeNo;

    /**
     * 线路编号
     */
    private String routeName;
    
    /**
     * 线路类型
     */
    private Integer routeType;
    
    /**
	 * 模板类型
	 */
	private Integer templateType;

    /**
     * 分配车辆
     */
    private Integer vehicleId;
    
    /**
     * 车牌号码
     */
    private String lpno;
    
    /**
     * 车牌号
     */
    private String seqno;

    /**
     * 任务日期
     */
    private Long routeDate;

    /**
     * 计划开始时间
     */
    private Long planBeginTime;

    /**
     * 计划结束时间
     */
    private Long planFinishTime;

    /**
     * 实际开始时间
     */
    private Long actBeginTime;

    /**
     * 实际结束时间
     */
    private Long actFinishTime;

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
     * 跟车人员
     */
    private Long follower;
    
    private String followerName;

    /**
     * 配钞-操作员
     */
//    private Long dispOperMan;
    
    private String dispOperManName;

    /**
     * 配钞-复核员
     */
//    private Long dispCheckMan;
    
    private String dispCheckManName;

    /**
     * 配钞时间
     */
    private Long dispTime;

    /**
     * 配钞复核时间
     */
    private Long dispCfmTime;


    /**
     * 钞袋数量
     */
    private Integer dispBagCount;

    /**
     * 车间交接-操作员
     */
//    private Long hdoverOperMan;
    
    private String hdoverOperManName;

    /**
     * 车间交接-复核员
     */
//    private Long hdoverCheckMan;
    
    private String hdoverCheckManName;

    /**
     * 车间交接时间
     */
    private Long hdoverTime;

    /**
     * 交接钞盒数量
     */
    private Integer returnBoxCount;

    /**
     * 交接钞袋数量
     */
    private Integer returnBagCount;

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
     * 交接调整标识
     */
    private Integer handoverChange;
    
    /**
     * 人员调整标识
     */
    private Integer empChange;

    /**
     * 备注
     */
    private String comments;

    /**
     * 审核人
     */
//    private Long checkUser;
    
    private String checkUserName;

    /**
     * 审核时间
     */
    private Long checkTime;
    
    /**
     * 车长日志
     */
    private Integer leaderLog;

}
