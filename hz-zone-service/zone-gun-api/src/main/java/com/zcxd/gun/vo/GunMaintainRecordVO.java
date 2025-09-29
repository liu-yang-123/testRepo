package com.zcxd.gun.vo;

import lombok.Data;

/**
 * @author zccc
 */
@Data
public class GunMaintainRecordVO {
    /**
     * 1：擦拭
     * 2：分解
     */
    private Integer operateType;

    /**
     * 枪支类型
     */
    private Long gunId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 部门
     */
    private Integer departmentId;
}
