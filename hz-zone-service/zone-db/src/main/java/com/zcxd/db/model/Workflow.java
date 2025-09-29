package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import java.io.Serializable;

/**
 * @author songanwei
 * @date 2021-06-09
 */
@Data
@TableName("workflow")
public class Workflow extends Model<Workflow> {

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 事件ID
     */
    private Long eventId;

    /**
     * 事件节点名称
     */
    private String nodeName;

    /**
     * 用户ID,逗号分隔
     */
    private String userIds;

    /**
     * 节点排序
     */
    private Integer sort;

    @Override
    protected Serializable pkVal() {
        return super.pkVal();
    }
}
