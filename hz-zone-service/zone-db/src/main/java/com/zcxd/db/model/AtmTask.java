package com.zcxd.db.model;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * ATM清机任务
 * </p>
 *
 * @author admin
 * @since 2021-07-22
 */
@ToString
@Data
public class AtmTask extends Model<AtmTask> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务编号
     */
    private String taskNo;

    /**
     * 终端ID
     */
    private Long atmId;

    /**
     * 设备所属银行
     */
    private Long bankId;

    /**
     * 设备网点
     */
    private Long subBankId;

    /**
     * 任务列表（1,2,3,4)  对应 维护，加钞，清机，巡检
     */
    private Integer taskType;

    /**
     * 任务日期
     */
    private Long taskDate;

    /**
     * 分配线路
     */
    private Long routeId;
    
    /**
     * 所属顶级部门
     */
    private Long departmentId;

    /**
     * 执行线路id
     */
    private Long carryRouteId;

    /**
     * 导入批次
     */
    private Long importBatch;

    /**
     * 回笼现金运送方式：0 - 钞袋， 1- 钞盒
     */
    private Integer carryType;

    /**
     * 加钞金额
     */
    private BigDecimal amount;

    /**
     * 加钞券别
     */
    private Long denomId;

    /**
     * 加钞钞盒列表
     */
    private String cashboxList;

    /**
     * 现场清点标志
     */
    private Integer clearSite;

    /**
     * 备用金排班标志 0 - 预排班， 1 - 备用金排班
     */
    private Integer backupFlag;

    /**
     * 任务状态
     */
    private Integer statusT;

    /**
     * 开始时间
     */
    private Long beginTime;

    /**
     * 结束时间
     */
    private Long endTime;
    /**
     * ATM运行状态
     */
    private Integer atmRunStatus;
    /**
     * 卡钞金额
     */
    private BigDecimal stuckAmount;

    /**
     * 清机密码员
     */
    private Long cleanOpManId;

    /**
     * 清机钥匙员
     */
    private Long cleanKeyManId;

    /**
     * 维修计划时间
     */
    private Long repairPlanTime;

    /**
     * 维修内容
     */
    private String repairContent;

    /**
     * 维修公司名称
     */
    private String repairCompany;

    /**
     * 离线上送标志
     */
    private Integer offline;

    /**
     * 任务渠道方式（0 - 后台，1 - 手机）
     */
    private Integer channel;
    /**
     * 任务备注
     */
    private String comments;

    /**
     * 设备巡检结果
     */
    private Integer checkResult;
    /**
     * 设备巡检详情
     */
    private String checkItemResult;

    /**
     * 创建人
     */
//    @TableField(fill= FieldFill.INSERT)
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Long createTime;

    /**
     * 更新人
     */
//    @TableField(fill= FieldFill.UPDATE)
    private Long updateUser;

    /**
     * 更新时间
     */
//    @TableField(fill= FieldFill.UPDATE)
    private Long updateTime;

    /**
     * 删除标志
     */
    private Integer deleted;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    /**
     * 是否执行出库动作
     */
    private Integer isOut;

}
