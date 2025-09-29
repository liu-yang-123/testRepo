package com.zcxd.base.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName HandoverLogVO
 * @Description 交班日志
 * @author 秦江南
 * @Date 2022年5月13日下午3:08:22
 */
@ApiModel("交班日志")
@Data
public class HandoverLogVO extends Model<HandoverLogVO> {

	@ApiModelProperty(value = "日志Id",required = false)
    private Long id;

    /**
     * 事业部ID
     */
	@ApiModelProperty(value = "所属部门",required = true)
	@NotNull(message = "所属部门不能为空")
    private Long departmentId;

    /**
     * 标题
     */
	@ApiModelProperty(value = "标题",required = true)
	@NotBlank(message = "标题不能为空")
	@Size(max=32,message="标题最大长度为32")
    private String title;

    /**
     * 内容
     */
	@ApiModelProperty(value = "内容",required = true)
	@NotBlank(message = "内容不能为空")
    private String contents;

    /**
     * 图片
     */
	@ApiModelProperty(value = "图片",required = false)
    private String imageUrl;

    /**
     * 文件名称
     */
	@ApiModelProperty(value = "文件名称",required = false)
    private String fileName;

    /**
     * 附件文件
     */
	@ApiModelProperty(value = "附件文件",required = false)
    private String fileUrl;

}
