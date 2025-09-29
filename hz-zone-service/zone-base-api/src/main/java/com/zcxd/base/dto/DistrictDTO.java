package com.zcxd.base.dto;

import lombok.Data;

/**
 *
 * @ClassName DistrictDTO
 * @Description 区域信息
 * @author liuyang
 * @Date 2025年9月21日下午2:41:29
 */
@Data
public class DistrictDTO {
    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 区域名称
     */
    private String districtName;
}
