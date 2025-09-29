package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 
 * @ClassName SchdVacateSettingVO
 * @Description 休息计划
 * @author shijin
 * @Date 2021年7月5日下午3:41:10
 */
@ApiModel("休息计划VO")
@Data
public class SchdVacateSettingVO {

	@ApiModelProperty(value = "休息计划id",required = true)
	@NotNull(message = "休息计划id不能为空")
    private Long planId;

	@ApiModelProperty(value = "人员列表",required = true)
	@NotNull(message = "人员列表不能为空")
    private List<SchdVacateSettingItemVO> vacatePlanItemVOS;
}
