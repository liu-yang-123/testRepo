package com.zcxd.gun.dto;

import lombok.Data;

/**
 * @author zccc
 */
@Data
public class GunCategoryDTO {
    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 枪支类型名称
     */
    private String gunCategoryName;

    /**
     * 备注
     */
    private String remark;
}
