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
public class GunSummary  extends Model<GunSummary> {
    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 枪弹名称
     */
    private String gunName;

    /**
     * 编号
     */
    private Long gunNum;

    /**
     * 总量
     */
    private Integer total;

    /**
     * 1：枪支
     * 2：弹盒
     */
    private Integer gunType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作员
     */
    private String operatorName;

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
