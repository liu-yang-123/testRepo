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
public class AttendanceMachine extends Model<AttendanceMachine> {
    private static final long serialVersionUID=1L;
    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * ip
     */
    private String address;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 机器号
     */
    private Integer machineNum;
    /**
     * 状态
     * 0：不可用
     * 1：可用
     */
    private Short status;
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
