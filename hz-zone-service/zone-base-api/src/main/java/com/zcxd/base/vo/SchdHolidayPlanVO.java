package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 
 * @ClassName SchdHolidayPlanVO
 * @Description 放假调班计划VO
 * @author shijin
 * @Date 2021年7月5日下午3:41:10
 */
@ApiModel("放假调班计划VO")
@Data
public class SchdHolidayPlanVO {
    /**
     * 自增id
     */
	@ApiModelProperty(value = "放假计划id",required = false)
    private Long id;

	@ApiModelProperty(value = "计划日期",required = true)
    @NotNull(message = "日期不能为空")
    private Long planDay;

    @ApiModelProperty(value = "放假/调班（0 - 放假，1 - 调班）",required = true)
    @NotNull(message = "放假类型不能为空")
    private Integer holidayType;

    @ApiModelProperty(value = "计划说明")
    private String comments;

    @ApiModelProperty(value = "调班星期",required = true)
    @NotNull(message = "调班星期不能为空")
    private Integer weekday;
}
