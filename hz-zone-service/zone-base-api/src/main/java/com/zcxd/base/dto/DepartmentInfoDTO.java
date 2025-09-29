package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName DepartmentInfoDTO
 * @Description 部门详细信息
 * @author 秦江南
 * @Date 2021年5月13日下午3:00:09
 */
@Data
public class DepartmentInfoDTO {

    /**
     * 部门名称
     */
    private String name;

    /**
     * 部门描述
     */
    private String description;

    /**
     * 上级部门
     */
    private String parentName;

    /**
     * 负责人姓名
     */
    private String linkmanName;

    /**
     * 联系电话
     */
    private String linkmanMobile;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;
}
