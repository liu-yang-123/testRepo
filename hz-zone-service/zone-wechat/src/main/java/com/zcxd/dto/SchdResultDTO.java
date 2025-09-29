package com.zcxd.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 *
 * @ClassName SchdResultDTO
 */
@Data
public class SchdResultDTO {
    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 计划日期
     */
    private Long planDay;

    /**
     * 线路组别
     */
    private String routeNo;

    private Long departmentId;
    private String empName;

    private String planDayStr;
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

    /**前端返回字段*/
    /**
     * 司机名称
     */
    private String driverName;

    /**
     * 护卫1名称
     */
    private String scurityAName;

    /**
     * 护卫2名称
     */
    private String scurityBName;

    /**
     * 钥匙员名称
     */
    private String keyManName;

    /**
     * 密码操作员名称
     */
    private String opManName;

    /**
     * 车长名称
     */
    private String leaderName;
}