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
 * 司机主替班配置
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@ToString
@Data
public class SchdAlternateAssign extends Model<SchdAlternateAssign> {

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
     * 计划ID
     */
    private Long planId;

    /**
     * 司机
     */
    private Integer planType;

    /**
     * 主备版类型（ 0 - 主班，1 - 替班）
     */
    private Integer alternateType;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 对应线路ID字符串，逗号分隔
     */
    private String routeIds;

    /**
     * 车牌号码,字符串逗号分隔
     */
    private String vehicleNos;


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
    private Integer deleted;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
