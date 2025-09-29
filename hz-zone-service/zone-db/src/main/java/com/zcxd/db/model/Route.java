package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;

/**
 * <p>
 * 线路任务记录
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
@Data
public class Route extends Model<Route> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 线路编号
     */
    private String routeNo;

    /**
     * 线路编号
     */
    private String routeName;
    
    /**
     * 线路唯一编号
     */
    private String routeNumber;
    
    /**
     * 线路类型
     */
    private Integer routeType;
    
	/**
	 * 模板类型
	 */
	private Integer templateType;
    
    /**
     * 所属顶级部门
     */
    private Long departmentId;

    /**
     * 分配车辆
     */
    private Integer vehicleId;

    /**
     * 任务日期
     */
    private Long routeDate;

    /**
     * 计划开始时间
     */
    private Long planBeginTime;

    /**
     * 计划结束时间
     */
    private Long planFinishTime;

    /**
     * 实际开始时间
     */
    private Long actBeginTime;

    /**
     * 实际结束时间
     */
    private Long actFinishTime;

    /**
     * 司机
     */
    private Long driver;

    /**
     * 护卫A
     */
    private Long securityA;

    /**
     * 护卫B
     */
    private Long securityB;

    /**
     * 业务-钥匙员
     */
    private Long routeKeyMan;

    /**
     * 业务-清机员
     */
    private Long routeOperMan;

    /**
     * 跟车人员
     */
    private Long follower;

    /**
     * 配钞-操作员
     */
    private Long dispOperMan;

    /**
     * 配钞-复核员
     */
    private Long dispCheckMan;

    /**
     * 配钞时间
     */
    private Long dispTime;

    /**
     * 配钞复核时间
     */
    private Long dispCfmTime;

    /**
     * 钞盒列表
     */
    private String dispBoxList;

    /**
     * 钞袋数量
     */
    private Integer dispBagCount;

    /**
     * 车间交接-操作员
     */
    private Long hdoverOperMan;

    /**
     * 车间交接-复核员
     */
    private Long hdoverCheckMan;

    /**
     * 车间交接时间
     */
    private Long hdoverTime;

    /**
     * 交接钞盒数量
     */
    private Integer returnBoxCount;

    /**
     * 交接钞袋数量
     */
    private Integer returnBagCount;

    /**
     * 线路状态
     */
    private Integer statusT;

    /**
     * 总任务数
     */
    private Integer taskTotal;

    /**
     * 完成任务数
     */
    private Integer taskFinish;

    /**
     * 备注
     */
    private String comments;

    /**
     * 审核人
     */
    private Long checkUser;

    /**
     * 审核时间
     */
    private Long checkTime;
    
    /**
     * 交接调整标识
     */
    private Integer handoverChange;
    
    /**
     * 人员调整标识
     */
    private Integer empChange;

    /**
     * 车长日志
     */
    private Integer leaderLog;
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
//    @TableField(fill= FieldFill.INSERT_UPDATE)
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

}
