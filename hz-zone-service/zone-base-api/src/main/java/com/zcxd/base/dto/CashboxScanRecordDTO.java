package com.zcxd.base.dto;

import lombok.Data;

@Data
public class CashboxScanRecordDTO {
    /**
     * 扫描节点
     */
    private Integer scanNode;
    /**
     * 扫描人
     */
    private String scanUser;
    /**
     * 扫描时间
     */
    private Long scanTime;
}
