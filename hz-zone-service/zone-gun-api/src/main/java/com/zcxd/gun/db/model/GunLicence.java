package com.zcxd.gun.db.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * 枪证
 * @author zccc
 */
@Data
public class GunLicence extends Model<GunLicence> {
    private static final long serialVersionUID = 1L;

    /**
     * 自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 员工Id
     */
    private Long employeeId;

    /**
     * 持枪证号
     */
    private String gunLicenceNum;

    /**
     * 持枪证有效期
     */
    private Long gunLicenceValidity;

    /**
     * 证件图片地址
     */
    private String gunLicencePhoto;

    /**
     * 备注
     */
    private String remark;

    /**
     * 证件管理状态
     */
    private String licenceStatus;

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
     * 部门
     */
    private Integer departmentId;

    /**
     * 删除标志
     */
    @TableLogic
    private Integer deleted;
}
