package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 
 * @ClassName SchdVacatePlanItemVO
 * @Description 休息计划记录
 * @author shijin
 * @Date 2021年7月5日下午3:41:10
 */
@ApiModel("休息计划记录VO")
@Data
public class SchdVacateSettingItemVO {

    /**
     * 自增id
     */
	@ApiModelProperty(value = "设置id",required = false)
    private Long id;

	@ApiModelProperty(value = "员工id",required = false)
    private Long empId;

	@ApiModelProperty(value = "休息日",required = true)
	@NotNull(message = "休息日不能为空")
    private List<Integer> weekdays;
}
