package com.zcxd.base.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName RouteTemplateVO
 * @Description 线路模板
 * @author 秦江南
 * @Date 2021年5月28日下午2:14:48
 */
@ApiModel("线路模板")
@Data
public class RouteTemplateVO {
	
    /**
     * 唯一标识
     */
	@ApiModelProperty(value = "线路模板Id",required = false)
    private Long id;
	
    /**
     * 所属顶级部门
     */
	@ApiModelProperty(value = "所属顶级部门",required = true)
    @NotNull(message = "所属顶级部门不能为空")  
    private Long departmentId;
    
    /**
     * 线路编号
     */
	@ApiModelProperty(value = "线路编号",required = true)
    @NotBlank(message = "线路编号不能为空")  
    @Size(max=32,message="routeNo最大长度为32")
    private String routeNo;

    /**
     * 线路名称
     */
	@ApiModelProperty(value = "线路名称",required = true)
    @NotBlank(message = "线路名称不能为空")  
    @Size(max=32,message="routeName最大长度为32")
    private String routeName;

//    /**
//     * 计划开始时间
//     */
//	@ApiModelProperty(value = "计划开始时间",required = true)
//    @NotNull(message = "计划开始时间不能为空")  
//    private String planBeginTime;
//
//    /**
//     * 计划结束时间
//     */
//	@ApiModelProperty(value = "计划结束时间",required = true)
//    @NotNull(message = "计划结束时间不能为空")  
//    private String planFinishTime;
	
	/**
     * 线路类型
     */
	@ApiModelProperty(value = "线路类型",required = true)
    @NotNull(message = "线路类型")  
    private Integer routeType;
    
    /**
     * 线路生成规则
     */
	@ApiModelProperty(value = "线路生成规则",required = true)
    private Integer rule;
    
    /**
     * 线路生成标志
     */
	@ApiModelProperty(value = "线路生成标志",required = false)
    private Integer sign;
	
    /**
     * 排序
     */
	@ApiModelProperty(value = "排序",required = true)
	@NotNull(message = "排序")  
    private Integer sort;
    
    /**
     * 备注
     */
	@ApiModelProperty(value = "备注",required = false)
    @Size(max=64,message="comments最大长度为64")
    private String comments;
    
}
