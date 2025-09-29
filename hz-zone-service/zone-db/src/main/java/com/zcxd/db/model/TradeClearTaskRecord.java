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
 * 清分任务明细
 * </p>
 *
 * @author admin
 * @since 2022-05-13
 */
@Data
public class TradeClearTaskRecord extends Model<TradeClearTaskRecord> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 区域清分任务id
     */
    private Long taskId;

    /**
     * 券别
     */
    private Long denomId;

    /**
     * 清分标志：0 - 可用券，1 - 残损券，2 - 五好券, 3 - 未清分, 4 - 尾零钞
     */
    private Integer gbFlag;

    /**
     * 总金额
     */
    private BigDecimal amount;
    
    /**
     * 张数
     */
    private Integer count;

    /**
     * 备注
     */
    private String comments;

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
