package com.zcxd.base.vo;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName ATMTaskCleanImportSaveVO
 * @Description 导入批量清机任务添加
 * @author 秦江南
 * @Date 2021年8月4日下午3:39:23
 */
@ApiModel("导入批量清机任务添加")
@Data
public class ATMTaskCleanImportSaveVO {
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
     * 导入批量清机任务
     */
	@ApiModelProperty(value = "导入批量清机任务",required = true)
	@NotNull(message = "导入批量清机任务不能为空")
    private List<ATMTaskCleanImportBatchVO> atmTaskCleanImportBatchVOs;
}
