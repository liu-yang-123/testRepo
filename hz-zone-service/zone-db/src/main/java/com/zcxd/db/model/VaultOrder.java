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
 * 出入库单
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
@ToString
@Data
public class VaultOrder extends Model<VaultOrder> {

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
     * 0-入库、1 - 出库
     */
    private Integer orderType;

    /**
     * 出入库子类型 0-ATM加钞 1-领缴款
     */
    private Integer subType;

    /**
     * 单据日期
     */
    private Long orderDate;

    /**
     * 单据金额
     */
    private BigDecimal orderAmount;

    /**
     * 凭证图片
     */
    private String voucherUrl;

    /**
     * 下一步审核人用户id,逗号分隔
     */
    private String nextUserIds;
    /**
     * 状态：录入、已审核
     */
    private Integer statusT;
    /**
     * 订单出入库完成情况：0 - 未完成、1 - 已完成
     */
    private Integer finish;

    /**
     * 备注
     */
    private String comments;

    /**
     * 操作员
     */
    private Long whOpMan;

    /**
     * 复核员
     */
    private Long whCheckMan;

    /**
     * 确认人
     */
    private Long whConfirmMan;

    /**
     * 出入库时间
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

    /**
     * 逻辑删除
     */
    private Integer deleted;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
