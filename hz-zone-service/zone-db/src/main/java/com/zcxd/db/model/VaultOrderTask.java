package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-08-10
 */
@Data
public class VaultOrderTask extends Model<VaultOrderTask> {

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
     * 类别 0-ATM任务 1-备用金
     */
    private Integer category;

    /**
     * 线路ID
     */
    private Long routeId;

    /**
     * 备用金
     */
    private Long bankId;

    /**
     * ATM加钞任务ID
     */
    private Long taskId;

    /**
     * ATM设备ID
     */
    private Long atmId;

    /**
     * 券别ID
     */
    private Long denomId;

    /**
     * 加钞金额
     */
    private BigDecimal amount;

    /**
     * 创建时间
     */
    private Long createTime;
}
