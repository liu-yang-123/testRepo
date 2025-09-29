package com.zcxd.base.dto;

import lombok.Data;

import java.util.Set;

/**
 * 
 * @ClassName WxNoticeMessage
 * @Description 微信任务推送通知消息
 * @author shijin
 * @Date 2021年10月15日
 */
@Data
public class WxTaskNotice {

    private Integer opType; // 1 - 新增，2 - 撤销
    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 线路id
     */
    private Long routeId;

    private String taskDate;
}
