package com.zcxd.base.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 清分任务DTO
 * @ClassName TradeClearTaskDTO
 * @Description TODO
 * @author 秦江南
 * @Date 2022年5月23日下午2:40:07
 */
@Data
public class TradeClearTaskDTO {
    /**
     * 自增id
     */
    private Long id;
    
    /**
     * 机构id
     */
    private Long bankId;

    /**
     * 机构名称
     */
    private String bankName;

    /**
     * 清分类型： 1 - 领现， 2 - 回笼， 3 - 尾箱
     */
    private Integer clearType;

    /**
     * 有无明细： 0 - 无， 1 - 有
     */
    private Integer haveDetail;

    /**
     * 任务金额
     */
    private BigDecimal totalAmount;

    /**
     * 实际清分金额
     */
    private BigDecimal realityAmount;

    /**
     * 任务日期
     */
    private Long taskDate;

    /**
     * 任务状态：-1 - 已撤销，0 - 已创建，1 - 已确认， 2 - 已完成
     */
    private Integer status;

    /**
     * 备注
     */
    private String comments;
}
