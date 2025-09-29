package com.zcxd.pda.dto;

import lombok.Data;

import java.util.List;

/**
 * 按上交网点分组显示吞没卡
 */
@Data
public class AtmTaskCardGroupDTO {

    /**
     * 银行网点id
     */
    private Long bankId;
    /**
     * 网点名称
     */
    private String bankName;

    /**
     * 任务列表
     */
    private List<AtmTaskCardGroupItem> groupList;
}
