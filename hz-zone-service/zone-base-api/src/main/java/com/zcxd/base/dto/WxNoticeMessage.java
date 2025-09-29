package com.zcxd.base.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 
 * @ClassName WxNoticeMessage
 * @Description 微信推送通知消息
 * @author shijin
 * @Date 2021年10月15日
 */
@Data
public class WxNoticeMessage {
    /**
     * 消息模板
     */
    private int template;
    /**
     * 推送员工
     */
    private Set<Long> empIds;

    /**
     * 日期
     */
    private String routeDate;

    /**
     * 线路
     */
    private String routeNo;

    /**
     * 车牌
     */
    private String vehicleNo;
    /**
     * 组员
     */
    private String empNames;

    /**
     * 状态(停用，使用）
     */
    private String changeText;

    /**
     * 备注
     */
    private String comments;
}
