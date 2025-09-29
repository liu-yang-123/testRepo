package com.zcxd.base.vo;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName FileRecordVO
 * @Description 文件传输记录
 * @author 秦江南
 * @Date 2022年11月11日上午11:03:56
 */
@ApiModel("文件传输记录")
@Data
public class FileRecordVO {

    /**
     * 接收单位
     */
	@ApiModelProperty(value = "接收单位",required = true)
    private Long companyId;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题",required = true)
    @NotBlank(message = "标题不能为空")
    private String recordTitle;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称",required = true)
    @NotBlank(message = "文件名称不能为空")
    private String fileName;

    /**
     * 文件地址
     */
    @ApiModelProperty(value = "文件地址",required = true)
    @NotBlank(message = "文件地址不能为空")
    private String fileUrl;

}
