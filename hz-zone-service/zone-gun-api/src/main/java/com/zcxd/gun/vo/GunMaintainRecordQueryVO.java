package com.zcxd.gun.vo;

import lombok.Data;

/**
 * @author zccc
 */
@Data
public class GunMaintainRecordQueryVO {
    /**
     * 操作时间
     */
    private Long operateTime;

    /**
     * 枪号/弹盒号
     */
    private String gunCode;

    /**
     * 内部编号
     */
    private Long internalNum;

    /**
     * 1：擦拭
     * 2：分解
     */
    private Integer operateType;

    /**
     * 枪支类型
     */
    private Integer gunType;

    private Integer departmentId;
}
