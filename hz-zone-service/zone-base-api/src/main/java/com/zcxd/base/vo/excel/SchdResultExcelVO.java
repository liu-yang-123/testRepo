package com.zcxd.base.vo.excel;

import lombok.Data;

/**
 * @author songanwei
 * @date 2021-08-17
 */
@Data
public class SchdResultExcelVO {

    /**
     * 线路编号
     */
    private String routeNo;

    /**
     * 用户A
     */
    private String userA;

    /**
     * 通行证编号
     */
    private String passportA;

    /**
     * 联系电话
     */
    private String phoneA;

    /**
     * 用户B
     */
    private String userB;

    /**
     * 通行证编号B
     */
    private String passportB;

    /**
     * 联系电话B
     */
    private String phoneB;

    /**
     * 车牌号码
     */
    private String carNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 代理车长
     */
    private String leader;
}
