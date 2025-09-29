package com.zcxd.pda.dto;

import lombok.Data;

/**
 * 线路车长日志DTO
 */
@Data
public class RouteLogItem {
    private String code; // 字段名
    /**
     * 检查结果
     */
    private Integer chk; // 0 - 否，1 - 是）

    /**
     * 备注
     */
    private String cmt; // 备注

}
