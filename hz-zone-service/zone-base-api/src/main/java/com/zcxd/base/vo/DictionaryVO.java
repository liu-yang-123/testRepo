package com.zcxd.base.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName SysDictionaryVO
 * @Description 数据字典
 * @author 秦江南
 * @Date 2021年5月14日上午9:41:32
 */
@ApiModel("数据字典")
@Data
public class DictionaryVO {
	/**
     * 唯一标识
     */
	@ApiModelProperty(value = "数据字典Id",required = false)
	private Long id;
	
    /**
     * 编号
     */
	@ApiModelProperty(value = "编号",required = true)
    @NotBlank(message = "编号不能为空")  
    @Size(max=32,message="code最大长度为32")
    private String code;

    /**
     * 分类名称
     */
	@ApiModelProperty(value = "分类名称",required = true)
    @NotBlank(message = "分类名称不能为空")  
    @Size(max=32,message="groups最大长度为32")
    private String groups;

    /**
     * 内容
     */
	@ApiModelProperty(value = "内容",required = true)
    @NotBlank(message = "内容不能为空")  
    @Size(max=32,message="content最大长度为32")
    private String content;


    /**
     * 备注
     */
	@ApiModelProperty(value = "备注",required = false)
    @Size(max=32,message="comments最大长度为32") 
    private String comments;
}
