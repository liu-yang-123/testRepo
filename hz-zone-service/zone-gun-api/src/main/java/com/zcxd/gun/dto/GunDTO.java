package com.zcxd.gun.dto;

import lombok.Data;

/**
 * @author zccc
 */
@Data
public class GunDTO {
    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 枪号/弹盒号
     */
    private String gunCode;

    /**
     * 持枪证号
     */
    private String gunLicenceNum;

    /**
     * 枪支类型
     */
    private String gunCategoryName;

    /**
     * 内部编号
     */
    private Long internalNum;

    /**
     * 配发日期
     */
    private String buyDate;

    /**
     * 枪型/弹盒
     */
    private Integer gunType;

    /**
     * 1：库存
     * 2：已发出
     * 3：禁用
     */
    private Integer gunStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 出勤次数
     */
    private Integer userCount;

    /**
     * 擦拭日期
     */
    private Long cleanDate;

    /**
     * 分解日期
     */
    private Long checkDate;

    /**
     * 操作员
     */
    private String operatorName;
}
