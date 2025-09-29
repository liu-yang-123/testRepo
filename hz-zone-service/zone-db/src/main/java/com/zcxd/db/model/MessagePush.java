package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 消息推送详情信息
 * @author lilanglang
 * @date 2021-10-20 15:43
 */
@Data
@TableName("message_push")
public class MessagePush extends Model<MessagePush> {

    /**
     * 线路排班
     */
    public final static int ROUTE=1;
    /**
     * 人员调整
     */
    public final static int EMPLOYEE=2;

    /**
     * 任务推送
     */
    public final static int TASK=3;
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 任务日期
     */
    private String routeDate;
    /**
     * 车辆
     */
    private String lpno;

    /**
     * 推送消息类型
     */
    private Integer type;

    /**
     * 司机
     */
    private Long driver;

    /**
     * 护卫A
     */
    private Long securityA;

    /**
     * 护卫B
     */
    private Long securityB;

    /**
     * 业务-钥匙员
     */
    private Long routeKeyMan;

    /**
     * 业务-清机员
     */
    private Long routeOperMan;
    /**
     * 推送人员
     */
    private String name;
    /**
     * 线路
     */
    private String routeName;
    /**
     * 人员变动信息
     */
    private String changeMan;
    /**
     * 任务网点
     */
    private String bank;
    /**
     * 任务设备
     */
    private String device;
    /**
     * 加钞金额
     */
    private BigDecimal amount;
    /**
     * 任务类型
     */
    private String opType;

    /**
     * 变更类型
     */
    private String changeType;

    /**
     * 备注
     */
    private String comments;

    /**
     * 创建日期
     */
    private Long createTime;


}