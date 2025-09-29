package com.zcxd.base.dto;

import lombok.Data;

@Data
public class RouteBoxBagChangeDTO {
    /**
     * 变更类型
     */
    private String changeType;
    
    /**
     * 变更前数量
     */
    private Integer oldCount;
    
    /**
     * 变更后数量
     */
    private Integer newCount;
    
    /**
     * 备注
     */
    private String comments;
    
    /**
     * 创建时间
     */
    private Long createTime;
}
