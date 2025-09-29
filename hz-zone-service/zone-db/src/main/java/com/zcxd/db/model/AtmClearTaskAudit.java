package com.zcxd.db.model;

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
 * 清分任务
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
@ToString
@Data
public class AtmClearTaskAudit extends Model<AtmClearTaskAudit> {

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 清分任务ID
     */
    private Long clearTaskId;

    /**
     * 计划清分金额/库存金额
     */
    private BigDecimal planAmount;

    /**
     * 实际清点金额
     */
    private BigDecimal clearAmount;

    /**
     * 清分类型
     */
    private Integer clearType;

    /**
     * 所属部门ID
     */
    private Long departmentId;

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
     * 差错明细
     */
    private String errorList;

    /**
     * 审核状态 0=未审核  1=审核中 2=审核通过 3=审核拒绝
     */
    private Integer statusT;

    /**
     * 创建人
     */
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
    @TableField(fill= FieldFill.UPDATE)
    private Long updateTime;

    /**
     * 逻辑删除
     */
    private Integer deleted;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
