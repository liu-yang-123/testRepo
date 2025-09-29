package com.zcxd.db.model;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 金库盘点
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
@ToString
@Data
public class VaultCheck extends Model<VaultCheck> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 银行机构
     */
    private Long bankId;

    /**
     * 可用券_余额
     */
    private BigDecimal usableBalance;

    /**
     * 残损券_余额
     */
    private BigDecimal badBalance;

    /**
     * 五好券_余额
     */
    private BigDecimal goodBalance;

    /**
     * 未清分余额
     */
    private BigDecimal unclearBalance;

    /**
     * 尾零余额
     */
    private BigDecimal remnantBalance;

    /**
     * 可用券清点金额
     */
    private BigDecimal usableAmount;

    /**
     * 残损券清点金额
     */
    private BigDecimal badAmount;

    /**
     * 五号券清点金额
     */
    private BigDecimal goodAmount;

    /**
     * 未清分清点金额
     */
    private BigDecimal unclearAmount;
    /**
     * 尾零盘点金额
     */
    private BigDecimal remnantAmount;

    /**
     * 备注
     */
    private String comments;

    /**
     * 查库人1
     */
    private Long whOpMan;

    /**
     * 查库人2
     */
    private Long whCheckMan;

    /**
     * 查库人3
     */
    private Long whConfirmMan;

    /**
     * 盘点时间
     */
    private Long whOpTime;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    /**
     * 更新人
     */
    private Long updateUser;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
