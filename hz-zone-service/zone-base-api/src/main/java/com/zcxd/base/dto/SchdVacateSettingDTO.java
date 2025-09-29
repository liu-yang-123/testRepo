package com.zcxd.base.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 
 * @ClassName SchdVacateSettingDTO
 * @Description 员工休息设置
 * @author shijin
 * @Date 2021年7月5日下午3:41:10
 */
@ApiModel("员工休息配置")
@Data
public class SchdVacateSettingDTO {

    private Long id;

	private String planName;

	private String empName;

	private String jobName;

    private List<Integer> weekdays;

	private Long createTime;

	private Long updateTime;

	private String createUserName;

	private String updateUserName;
}
