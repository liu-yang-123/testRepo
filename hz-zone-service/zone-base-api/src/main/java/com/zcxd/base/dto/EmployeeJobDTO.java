package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName EmployeeJobDTO
 * @Description 岗位信息
 * @author 秦江南
 * @Date 2021年5月13日下午7:40:36
 */
@Data
public class EmployeeJobDTO {
    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 岗位名称
     */
    private String name;
    
    /**
     * 岗位类型
     */
    private Integer jobType;

    /**
     * 岗位描述
     */
    private String descript;
}
