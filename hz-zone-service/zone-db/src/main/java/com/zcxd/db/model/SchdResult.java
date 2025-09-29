package com.zcxd.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 排班结果
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@Data
public class SchdResult extends Model<SchdResult> {

    private static final long serialVersionUID=1L;

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
     * 计划日期
     */
    private Long planDay;

    /**
     * 线路组别
     */
    private String routeNo;

    /**
     * 线路类型 0-其他线路 1-60/61号线 2-8号线
     */
    private Integer routeType;

    /**
     * 车牌号码
     */
    private String vehicleNo;

    /**
     * 司机
     */
    private Long driver;

    /**
     * 护卫1
     */
    private Long scurityA;

    /**
     * 护卫2
     */
    private Long scurityB;

    /**
     * 钥匙员
     */
    private Long keyMan;

    /**
     * 密码操作员
     */
    private Long opMan;

    /**
     * 车长
     */
    private Long leader;

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
