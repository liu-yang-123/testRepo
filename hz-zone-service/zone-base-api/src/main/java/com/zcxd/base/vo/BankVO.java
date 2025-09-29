package com.zcxd.base.vo;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @ClassName BankVO
 * @Description 机构信息
 * @author 秦江南
 * @Date 2021年5月20日下午3:41:10
 */
@ApiModel("机构信息")
@Data
public class BankVO {

    /**
     * 自增id
     */
	@ApiModelProperty(value = "机构Id",required = false)
    private Long id;

    /**
     * 部门（事业部id）
     */
	@ApiModelProperty(value = "所属部门",required = true)
	@NotNull(message = "所属部门不能为空")
    private Long departmentId;
	
    /**
     * 父级机构ID: /0/aaa/bbb
     */
	@ApiModelProperty(value = "上级部门",required = true)
	@NotBlank(message = "父级机构不能为空")
    private String parentIds;

    /**
     * 机构名称
     */
    @ApiModelProperty(value = "机构名称",required = true)
    @NotBlank(message = "机构名称不能为空")
    @Size(max=64,message="机构名称最大长度为64")
    private String fullName;

    /**
     * 机构简称
     */
    @ApiModelProperty(value = "机构简称",required = true)
    @NotBlank(message = "机构简称不能为空")
    @Size(max=32,message="机构简称最大长度为32")
    private String shortName;

    /**
     * 省份
     */
    @ApiModelProperty(value = "省份",required = false)
    @Size(max=32,message="省份最大长度为32")
    private String province;

    /**
     * 城市
     */
    @ApiModelProperty(value = "城市",required = false)
    @Size(max=32,message="城市最大长度为32") 
    private String city;

    /**
     * 区县
     */
    @ApiModelProperty(value = "区县",required = false)
    @Size(max=32,message="区县最大长度为32")
    private String district;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址",required = false)
    @Size(max=64,message="详细地址最大长度为64")
    private String address;
    
    /**
     * 营业时间及应急联系人
     */
    @ApiModelProperty(value = "营业时间及应急联系人",required = false)
    @Size(max=64,message="营业时间及应急联系人最大长度为64")
    private String workInfo;
    
    /**
     * 线路编号
     */
    @ApiModelProperty(value = "线路编号",required = false)
    private String routeNo;

    /**
     * 位置经度
     */
    @ApiModelProperty(value = "位置经度",required = false)
    private BigDecimal locateLat;

    /**
     * 位置纬度
     */
    @ApiModelProperty(value = "位置纬度",required = false)
    private BigDecimal locateLng;
    
    /**
     * 回笼现金运送方式：0 - 钞袋， 1- 钞盒
     */
    @ApiModelProperty(value = "回笼现金运送方式",required = false)
    private Integer carryType;

    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人",required = false)
    @Size(max=32,message="联系人最大长度为32")
    private String contact;

    /**
     * 联系人电话
     */
    @ApiModelProperty(value = "联系人电话",required = false)
    @Size(max=32,message="联系人电话最大长度为32")
    private String contactPhone;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注",required = false)
    @Size(max=64,message="备注最大长度为64")
    private String comments;
    
    /**
     * 网点类型：1 - 总行，2 - 分行，3 - 支行，4 - 网点, 5 - 库房
     */
    @ApiModelProperty(value = "网点类型",required = false)
    private Integer bankLevel;

    /**
     * 机构种类：1 - 营业机构，2 - 非营业机构，3 - 库房
     */
    @ApiModelProperty(value = "机构种类",required = false)
    private Integer bankCategory;

    /**
     * 是否开通ATM加钞(0 - 不开通，1-开通）
     */
    private Integer haveAtm;
    /**
     * 是否开通尾箱业务(0 - 不开通，1-开通）
     */
    private Integer haveBox;
    /**
     * 是否开通商业清分(0 - 不开通，1-开通）
     */
    private Integer haveClear;
    /**
     * 是否开通金库业务(0 - 不开通，1-开通）
     */
    private Integer haveStore;
}
