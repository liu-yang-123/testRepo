package com.zcxd.gun.vo;

import lombok.Data;

/**
 * @author zccc
 */
@Data
public class GunLicenceQueryVO {
    /**
     * 员工姓名
     */
    private String name;

    /**
     * 持枪证号
     */
    private String gunLicenceNum;

    /**
     * 持枪证图片
     */
    private Boolean hasPhoto;

    /**
     * 是否有效
     */
    private Boolean isValid;

    /**
     * 部门Id
     */
    private Integer departmentId;
}
