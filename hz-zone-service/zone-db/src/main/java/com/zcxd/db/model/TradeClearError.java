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
 * 清分差错
 * </p>
 *
 * @author admin
 * @since 2022-05-13
 */
@Data
public class TradeClearError extends Model<TradeClearError> {

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
     * 银行支行
     */
    private Long bankId;

    /**
     * 差错支行网点名称
     */
    private String subBank;

    /**
     * 券别id
     */
    private Long denomId;

    /**
     * 发现日期/清分日期
     */
    private Long clearDate;

    /**
     * 长款笔数
     */
    private Integer cashOverCount;

    /**
     * 长款金额
     */
    private BigDecimal cashOverAmount;

    /**
     * 短款笔数
     */
    private Integer cashShortCount;

    /**
     * 短款金额
     */
    private BigDecimal cashShortAmount;

    /**
     * 假币笔数
     */
    private Integer fakeCount;

    /**
     * 假币金额
     */
    private BigDecimal fakeAmount;

    /**
     * 夹张笔数
     */
    private Integer carryCount;

    /**
     * 夹张金额
     */
    private BigDecimal carryAmount;

    /**
     * 错款人
     */
    private String errorMan;

    /**
     * 发现人
     */
    private String clearMan;

    /**
     * 复核人
     */
    private String checkMan;

    /**
     * 状态：-1 - 已取消，0 - 已创建，1 - 已确认
     */
    private Integer status;

    /**
     * 备注
     */
    private String comments;

    /**
     * 确认时间
     */
    private Long confirmTime;

    /**
     * 确认人
     */
    private Long confirmUser;

    /**
     * 备注
     */
    private String checkComments;

    /**
     * 撤销时间
     */
    private Long cancelTime;

    /**
     * 撤销人
     */
    private Long cancelUser;

    /**
     * 封签日期
     */
    private String sealDate;

    /**
     * 封签人
     */
    private String sealMan;

    /**
     * 创建日期
     */
    private String createDay;

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
