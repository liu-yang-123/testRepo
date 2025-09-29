package com.zcxd.base.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 线路备用金额DTO
 * </p>
 *
 * @author admin
 * @since 2021-08-11
 */
@ToString
@Data
public class AtmAdditionalCashDTO implements Serializable {

    private Long id;
    /**
     * 日期
     */
    private String taskDate;
    /**
     * 线路
     */
    private Long routeId;

    /**
     * 线路编号
     */
    private String routeNo;
    /**
     * 所属机构
     */
    private Long bankId;

    /**
     * 机构名称
     */
    private String bankName;

    /**
     * 金额类型 0-备用金 1-其他
     */
    private Integer cashType;

    /**
     * 券别面额
     */
    private Integer denomValue;
    /**
     * 券别名称
     */
    private String denomName;
    /**
     * 备用金金额
     */
    private BigDecimal amount;
    
    /**
     * 执行线路id
     */
    private Long carryRouteId;
    
    /**
     * 执行线路id
     */
    private String carryRouteNo;
    
    /**
     * 备注
     */
    private String comments;
    /**
     * 状态（0 - 创建，1- 确认，2 - 出库
     */
    private Integer statusT;


    private String statusText;
    /**
     * 创建人
     */
    private String createUserName;
    /**
     * 创建时间
     */
    private Long createTime;

}
