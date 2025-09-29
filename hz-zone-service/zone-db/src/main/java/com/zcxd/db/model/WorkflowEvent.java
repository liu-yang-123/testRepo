package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import java.io.Serializable;

/**
 * @author songanwei
 * @date 2021-06-09
 */
@Data
@TableName("workflow_event")
public class WorkflowEvent extends Model<WorkflowEvent> {

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
     * 事件名称
     */
    private String eventName;

    /**
     * 事件编码
     */
    private String eventCode;

    /**
     * 事件状态
     */
    private Integer status;

    /**
     * 事件消息状态
     */
    private Integer msgStatus;

    /**
     * 更新用户ID
     */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Long updateUser;

    /**
     * 更新时间
     */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Long updateTime;

    @Override
    protected Serializable pkVal() {
        return super.pkVal();
    }
}
