package com.zcxd.base.vo;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName FileCompanyVO
 * @Description 单位信息
 * @author 秦江南
 * @Date 2022年11月8日下午3:34:49
 */
@ApiModel("单位信息")
@Data
public class FileCompanyVO {

	@ApiModelProperty(value = "日志Id",required = false)
    private Long id;

    /**
     * 单位号
     */
	@ApiModelProperty(value = "单位号",required = true)
	@NotNull(message = "单位号不能为空")
    private String companyNo;

    /**
     * 单位名称
     */
	@ApiModelProperty(value = "单位名称",required = true)
	@NotNull(message = "单位名称不能为空")
    private String companyName;

    /**
     * 单位类别
     */
    @ApiModelProperty(value = "单位类别",required = true)
    @NotNull(message="单位类别不能为空")
    private Integer companyType;

    /**
     * 文件目录
     */
	@ApiModelProperty(value = "文件目录",required = true)
	@NotNull(message = "文件目录不能为空")
    private String fileDirectory;
	
    /**
     * 操作员
     */
	@ApiModelProperty(value = "操作员",required = true)
	@NotNull(message = "操作员不能为空")
    private String userIds;

}
