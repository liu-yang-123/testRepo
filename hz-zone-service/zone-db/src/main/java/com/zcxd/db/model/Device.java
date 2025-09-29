package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 设备信息
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
@ToString
@Data
public class Device extends Model<Device> {

    private static final long serialVersionUID=1L;

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 归属部门
     */
    private Long departmentId;

    /**
     * 设备编号
     */
    private String deviceNo;

    /**
     * 设备型号
     */
    private Long modelId;

    /**
     * 设备厂商
     */
    private Long factoryId;

    /**
     * 设备出厂SN
     */
    private String deviceSn;

    /**
     * 设备所在位置
     */
    private String location;

    /**
     * 设备ip地址
     */
    private String ipaddr;

    /**
     * 设备状态 0- 正常使用，1 - 维修中，2 - 报废
     */
    private String statusT;

    /**
     * 备注
     */
    private String comments;

    /**
     * 是否已分配 0 - 未分配，1 - 已分配
     */
    private Integer assigned;

    /**
     * 购买日期
     */
    private Long enrollDate;

    /**
     * 上次维保日期
     */
    private Long lastMaintainDate;

    /**
     * 下次维保日期
     */
    private Long nextMaintainDate;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新人
     */
    private Long updateUser;

    /**
     * 更新时间
     */
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
