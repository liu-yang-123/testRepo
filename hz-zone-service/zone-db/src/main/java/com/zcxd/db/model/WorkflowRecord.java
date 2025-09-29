package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import java.io.Serializable;

/**
 * @author songanwei
 * @date 2021-06-10
 */
@Data
@TableName("workflow_record")
public class WorkflowRecord extends Model<WorkflowRecord> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 事件ID
     */
    private Long eventId;

    /**
     * 审核标识ID
     */
    private Long identityId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 审核状态
     */
    private Integer status;

    /**
     * 审核顺序
     */
    private Integer sort;

    /**
     * 审核内容
     */
    private String comments;

    /**
     * 创建用户
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createTime;


    @Override
    protected Serializable pkVal() {
        return super.pkVal();
    }

}
