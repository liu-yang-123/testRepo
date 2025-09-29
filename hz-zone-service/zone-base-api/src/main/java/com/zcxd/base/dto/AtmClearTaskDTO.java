package com.zcxd.base.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-07-06
 */
@Data
public class AtmClearTaskDTO implements Serializable {

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 计划清分金额/库存金额
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal planAmount;

    /**
     * 实际清点金额
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal clearAmount;

    /**
     * 清分类型
     */
    private Integer clearType;

    /**
     * 线路（选填）
     */
    private Long routeId;

    /**
     * atm设备（选填）
     */
    private Long atmId;

    /**
     * 银行网点
     */
    private Long bankId;

    /**
     * 任务日期
     */
    private String taskDate;

    /**
     * 清点员
     */
    private Long clearMan;

    /**
     * 复核员
     */
    private Long checkMan;

    /**
     * 清点员
     */
    private String clearManName;

    /**
     * 复核员
     */
    private String checkManName;

    /**
     * 清点时间
     */
    private Long clearTime;

    /**
     * 差错类型
     */
    private Integer errorType;

    /**
     * 差错金额
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal errorAmount;

    /**
     * 差错备注
     */
    private String errorReason;
    /**
     * 差错确认人
     */
    private Long errorConfirmMan;
    /**
     * 备注
     */
    private String comments;

    /**
     * 状态
     */
    private Integer statusT;

    /**
     * 银行机构
     */
    private String bankName;

    /**
     * ATM设备编号
     */
    private String terNo;

    /**
     * 线路描述  线路编号/线路名称
     */
    private String routeText;

    /**
     * 是否当前审核用户
     */
    private Boolean audit;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 差错明细
     */
    private String errorList;
}
