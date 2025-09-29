package com.zcxd.base.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * 
 * @ClassName SchdVacateSettingSumDTO
 * @Description 员工休息设置统计DTO
 * @author shijin
 * @Date 2021年7月5日下午3:41:10
 */
@ApiModel("员工休息配置人数统计")
@Data
public class SchdVacateSettingSumDTO {

	private String jobName; //岗位名称
    private int countMon; //星期一数量
    private int countTue; //星期二数量
    private int countWed; //同上类推
    private int countThu; //同上类推
    private int countFri; //同上类推
    private int countSat; //同上类推
    private int countSun; //同上类推
}
