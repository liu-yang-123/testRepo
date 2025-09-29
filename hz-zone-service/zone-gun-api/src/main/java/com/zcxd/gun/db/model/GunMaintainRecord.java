package com.zcxd.gun.db.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

/**
 * @author zccc
 */
@ToString
@Data
public class GunMaintainRecord extends Model<GunMaintainRecord> {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作时间
     */
    private Long operateTime;

    /**
     * 1：擦拭
     * 2：分解
     */
    private Integer operateType;

    /**
     * 枪支类型
     */
    private Long gunId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作员
     */
    private String operator;

    /**
     * 部门
     */
    private Integer departmentId;

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
     * 删除标志
     */
    @TableLogic
    private Integer deleted;
}
