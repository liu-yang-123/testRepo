package com.zcxd.db.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;
import java.io.Serializable;

/**
 * <p>
 * 设备维保记录
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
@ToString
@Data
public class DeviceMaintain extends Model<DeviceMaintain> {

    private static final long serialVersionUID=1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备编号
     */
    private Long deviceId;

    /**
     * 维保日期
     */
    private Long mtDate;

    /**
     * 维保类型
     */
    private String mtType;

    /**
     * 故障原因
     */
    private String mtReason;

    /**
     * 维保内容
     */
    private String mtContent;

    /**
     * 维保成本
     */
    private BigDecimal mtCost;

    /**
     * 维保结果: 1 - 维修成功，0- 维修失败
     */
    private Integer mtResult;

    /**
     * 维保工程师
     */
    private String mtEngineer;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建人
     */
    private Long updateUser;

    /**
     * 创建时间
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
