package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;

/**
 * 
 * @ClassName RouteBoxBagChange
 * @Description 交接信息变更记录
 * @author 秦江南
 * @Date 2022年11月21日上午10:48:16
 */
@Data
public class RouteBoxbagChange extends Model<RouteBoxbagChange>{
	private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 线路id
     */
    private Long routeId;

    /**
     * 变更类型 0-钞盒 1-钞袋
     */
    private Integer changeType;
    
    /**
     * 变更前人员
     */
    private Integer oldCount;
    
    /**
     * 变更后人员
     */
    private Integer newCount;
    
    /**
     * 备注
     */
    private String comments;
    
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

}
