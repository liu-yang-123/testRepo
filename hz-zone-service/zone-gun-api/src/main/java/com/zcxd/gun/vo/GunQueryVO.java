package com.zcxd.gun.vo;

import lombok.Data;

/**
 * @author zccc
 */
@Data
public class GunQueryVO {
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
     * 枪支分类
     */
    private Long gunCategory;

    /**
     * 枪状态
     */
    private Integer gunStatus;

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

    private Integer departmentId;

    /**
     * 内部编号范围开头
     */
    private Long internalNumStart;
    /**
     * 内部编号范围结尾
     */
    private Long internalNumEnd;

}
