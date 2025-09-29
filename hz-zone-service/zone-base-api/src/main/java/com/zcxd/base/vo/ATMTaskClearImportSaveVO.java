package com.zcxd.base.vo;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName ATMTaskClearImportSaveVO
 * @Description 添加导入批量清机任务
 * @author 秦江南
 * @Date 2021年8月6日上午9:45:01
 */
@ApiModel("添加导入批量清机任务")
@Data
public class ATMTaskClearImportSaveVO {
	/**
     * 任务日期
     */
	@ApiModelProperty(value = "任务日期",required = true)
	@NotNull(message = "任务日期不能为空")
    private Long taskDate;
	
    /**
     * 部门
     */
	@ApiModelProperty(value = "部门",required = true)
	@NotNull(message = "部门不能为空")
    private Long departmentId;

    /**
     * 银行类型
     */
	@ApiModelProperty(value = "银行类型",required = true)
	@NotNull(message = "银行类型不能为空")
    private Integer bankType;

    /**
     * 文件名称
     */
	@ApiModelProperty(value = "文件名称",required = true)
	@NotBlank(message = "文件名称不能为空")
    private String fileName;
	
    /**
     * 系统文件名称
     */
	@ApiModelProperty(value = "系统文件名称",required = true)
	@NotBlank(message = "系统文件名称不能为空")
    private String systemFileName;
	
	/**
	 * 清分任务列表
	 */
	@ApiModelProperty(value = "清分任务列表",required = true)
	@NotNull(message = "清分任务列表不能为空")
	private List<ATMTaskClearImportBatchVO> atmTashClearList;
}
