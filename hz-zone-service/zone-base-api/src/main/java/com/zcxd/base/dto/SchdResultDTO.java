package com.zcxd.base.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
import java.util.ListResourceBundle;

/**
 *
 * @ClassName SchdResultDTO
 */
@ApiModel("线路排班结果")
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

    /**
     * 线路类型 0-其他线路 1-60/61号线 2-8号线
     */
    private Integer routeType;

    private Long departmentId;


    private String empName;
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
     * 钥匙员通行证
     */
    private String keyManCodeList;

    /**
     * 密码操作员
     */
    private Long opMan;

    /**
     * 密码员通行证
     */
    private String opManCodeList;

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