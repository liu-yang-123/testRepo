package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * ATM回笼记录
 * </p>
 *
 * @author admin
 * @since 2021-05-27
 */
@ToString
@Data
public class AtmTaskReturn extends Model<AtmTaskReturn> {

    private static final long serialVersionUID=1L;

    /**
     * 自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 清机任务id
     */
    private Long taskId;

    /**
     * atm id
     */
    private Long atmId;
    /**
     * bank id
     */
    private Long bankId;
    /**
     * atm 线路id
     */
    private Long routeId;
    /**
     * 钞盒,钞袋编号
     */
    private String boxBarCode;

    /**
     * 装运方式（0 - 钞袋，1 - 钞盒)
     */
    private Integer carryType;

    /**
     * 任务日期
     */
    private String taskDate;

    /**
     * 回笼钞盒钞袋清点标志( 0 - 未清点，1 - 已清点）
     */
    private Integer clearFlag;
    private Long clearTime;
    private Long clearManId;
    private Long checkManId;
    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

}
