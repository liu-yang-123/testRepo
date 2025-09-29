package com.zcxd.base.dto;

import lombok.Data;

@Data
public class RouteEmpChangeDTO {
    /**
     * 岗位类型
     */
    private Integer jobType;
    
    /**
     * 变更前人员
     */
    private String oldManName;
    
    /**
     * 变更后人员
     */
    private String newManName;
    
    /**
     * 备注
     */
    private String comments;
    
    /**
     * 创建时间
     */
    private Long createTime;
}
