package com.zcxd.db.model;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;

/**
 * <p>
 * 收费规则
 * </p>
 *
 * @author admin
 * @since 2022-05-22
 */
@Data
public class TradeClearChargeRule extends Model<TradeClearChargeRule> {

    private static final long serialVersionUID=1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属事业部
     */
    private Long departmentId;

    /**
     * 机构id
     */
    private Long bankId;

    /**
     * 券别类型：1 - 完整券，2 - 残损券
     */
    private Integer gbFlag;

    /**
     * 票面面额
     */
    private String denom;

    /**
     * 纸硬币(P - 纸币，C - 硬币）
     */
    private String attr;

    /**
     * 单价
     */
    private BigDecimal price;

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
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Long updateUser;

    /**
     * 更新时间
     */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Long updateTime;

    /**
     * 逻辑删除
     */
    private Integer deleted;

}
