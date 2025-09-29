package com.zcxd.base.dto;

import lombok.Data;

/**
 * 线路车长日志DTO
 */
@Data
public class RouteLogItem {
    private String code;
    /**
     * 检查结果
     */
    private Integer chk; // 0 - 异常，1 - 正常）

    /**
     * 备注
     */
    private String cmt;

}
