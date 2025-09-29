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
 * 清分任务
 * </p>
 *
 * @author admin
 * @since 2022-05-22
 */
@Data
public class TradeClearTask extends Model<TradeClearTask> {

    private static final long serialVersionUID=1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 事业部ID
     */
    private Long departmentId;

    /**
     * 机构id
     */
    private Long bankId;

    /**
     * 清分类型： 1 - 领现， 2 - 尾箱， 3 - 回笼
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
     * 任务状态：-1 - 已取消，0 - 已创建，1 - 已确认， 2 - 已完成
     */
    private Integer status;

    /**
     * 备注
     */
    private String comments;

    /**
     * 确认人
     */
    private Long confirmUser;

    /**
     * 确认时间
     */
    private Long confirmTime;

    /**
     * 撤销人
     */
    private Long cancelUser;

    /**
     * 撤销时间
     */
    private Long cancelTime;

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
