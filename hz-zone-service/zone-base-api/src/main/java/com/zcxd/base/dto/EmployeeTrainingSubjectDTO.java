package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName EmployeeTrainingSubjectDTO
 * @Description 培训主题
 * @author 秦江南
 * @Date 2021年5月18日下午4:37:16
 */
@Data
public class EmployeeTrainingSubjectDTO {

	/**
     * 自增主键
     */
    private Long id;

    /**
     * 培训日期
     */
    private Long trainDate;

    /**
     * 培训类别
     */
    private String trainType;

    /**
     * 培训主题
     */
    private String trainTitle;

    /**
     * 培训内容
     */
    private String trainContent;

    /**
     * 培训老师
     */
    private String trainer;

    /**
     * 培训地点
     */
    private String place;

    /**
     * 培训时长
     */
    private Integer times;

    /**
     * 考核方式
     */
    private String test;

}