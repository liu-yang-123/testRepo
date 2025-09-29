package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;

/**
 * <p>
 * 尾箱任务
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
@Data
public class BoxpackTask extends Model<BoxpackTask> {

    private static final long serialVersionUID=1L;

    /**
     * 自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务编号
     */
    private String taskNo;

    /**
     * 银行网点
     */
    private Long bankId;
    
    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 任务日期
     */
    private Long taskDate;

    /**
     * 任务备注
     */
    private String comments;
    
    /**
     * 线路编号
     */
    private String routeNo;

    /**
     * 分配线路
     */
    private Long routeId;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 尾箱列表
     */
    private String boxList;

    /**
     * 交箱列表
     */
    private String handBoxList;
    
    /**
     * 交接押运员
     */
    private String handEsortMans;
    
    /**
     * 交接押运员姓名
     */
    private String handEsortMansName;
    
    /**
     * 交接操作员
     */
    private String handOpMans;

    /**
     * 交接操作员姓名
     */
    private String handOpMansName;
    
    /**
     * 交接时间
     */
    private Long handTime;
    
    /**
     * 任务状态
     */
    private Integer statusT;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createTime;

    /**
     * 更新人
     */
    private Long updateUser;

    /**
     * 更新时间
     */
    @TableField(fill= FieldFill.UPDATE)
    private Long updateTime;

    /**
     * 删除标志
     */
    private Integer deleted;

}
