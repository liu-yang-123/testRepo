package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * <p>
 * 线路备用金额
 * </p>
 *
 * @author admin
 * @since 2021-08-11
 */
@ToString
@Data
public class AtmAdditionCash extends Model<AtmAdditionCash> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 所属机构
     */
    private Long bankId;

    /**
     * 券别id
     */
    private Long denomId;
    /**
     * 券别面额
     */
    private Integer denomValue;

    /**
     * 金额类型 0-备用金 1-其他
     */
    private Integer cashType;
    /**
     * 备用金金额
     */
    private BigDecimal amount;
    
    /**
     * 执行线路id
     */
    private Long carryRouteId;
    
    /**
     * 备注
     */
    private String comments;

    /**
     * 状态（0 - 创建，1- 确认，2 - 出库
     */
    private Integer statusT;

    /**
     * 所属顶级部门
     */
    private Long departmentId;

    /**
     * 创建人
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createUser;
    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createTime;
    /**
     * 更新人
     */
    private Long updateUser;
    /**
     * 更新时间
     */
    private Long updateTime;
    /**
     * 逻辑删除
     */
    private Integer deleted;

    /**
     * 是否执行出库动作
     */
    private Integer isOut;


}
