package com.zcxd.pda.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 线路车长日志DTO
 */
@Data
public class RouteLogDTO {
    private Long routeId;
    /**
     * 日志详情
     */
    private List<RouteLogItem> details;

    /**
     * 日志备注
     */
    private String comments;

}
