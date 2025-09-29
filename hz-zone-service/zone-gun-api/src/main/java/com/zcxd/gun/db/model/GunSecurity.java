package com.zcxd.gun.db.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * 保安证
 * @author zccc
 */
@Data
public class GunSecurity extends Model<GunSecurity> {
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
     * 保安证号
     */
    private String securityNum;

    /**
     * 保安证图片地址
     */
    private String securityPhoto;

    /**
     * 保安证派发机构
     */
    private String authority;

    /**
     * 保安证状态
     * 1：在库
     */
    private int status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 部门Id
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
