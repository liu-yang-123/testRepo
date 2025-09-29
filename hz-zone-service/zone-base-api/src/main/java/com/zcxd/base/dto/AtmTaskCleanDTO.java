package com.zcxd.base.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 
 * @ClassName AtmTaskCleanDTO
 * @Description 清机任务
 * @author 秦江南
 * @Date 2021年6月15日下午5:48:13
 */
@Data
public class AtmTaskCleanDTO{

    /**
     * 加钞金额
     */
    private BigDecimal amount;

    /**
     * 任务备注
     */
    private String comments;
    
    /**
     * 现场清点标志
     */
    private Integer clearSite;

    /**
     * 加钞钞盒列表
     */
    private List<Map> cashboxMap;
    
    /**
     * 回笼钞盒列表
     */
    private String[] barboxList;
    
    /**
     * ATM运行状态
     */
    private Integer atmRunStatus;
    
    /**
     * 卡钞金额
     */
    private BigDecimal stuckAmount;

    /**
     * 清机密码员
     */
    private String cleanOpManName;

    /**
     * 清机钥匙员
     */
    private String cleanKeyManName;
}
