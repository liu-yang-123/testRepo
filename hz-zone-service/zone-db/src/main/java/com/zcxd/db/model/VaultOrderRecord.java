package com.zcxd.db.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 出入库明细
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
@ToString
@Data
public class VaultOrderRecord extends Model<VaultOrderRecord> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 出入库单号
     */
    private Long orderId;

    /**
     * 库存类型:可用券，残损券，五好券
     */
    private Integer denomType;

    /**
     * 券别
     */
    private Long denomId;

    /**
     * 金额
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
    private Long createUser;

    /**
     * 创建时间
     */
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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }


}
