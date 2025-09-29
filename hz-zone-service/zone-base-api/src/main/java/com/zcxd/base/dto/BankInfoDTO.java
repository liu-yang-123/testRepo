package com.zcxd.base.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 
 * @ClassName BankInfoDTO
 * @Description 机构详细信息
 * @author 秦江南
 * @Date 2021年5月20日上午10:18:58
 */
@Data
public class BankInfoDTO {

    /**
     * 部门（事业部id）
     */
    private Long departmentId;
	
	/**
	 * 上级部门
	 */
    private String parentName;

    /**
     * 机构名称
     */
    private String fullName;

    /**
     * 机构简称
     */
    private String shortName;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;
    
    /**
     * 营业时间及应急联系人
     */
    private String workInfo;
    
    /**
     * 线路编号
     */
    private String routeNo;

    /**
     * 位置经度
     */
    private BigDecimal locateLat;

    /**
     * 位置纬度
     */
    private BigDecimal locateLng;

    /**
     * 回笼现金运送方式：0 - 钞袋， 1- 钞盒
     */
    private Integer carryType;
    
    /**
     * 机构状态
     */
    private Integer statusT;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 备注
     */
    private String comments;
    
    /**
     * 网点类型：1 - 总行，2 - 分行，3 - 支行，4 - 网点, 5 - 库房
     */
    private Integer bankLevel;

    /**
     * 机构种类：1 - 营业机构，2 - 非营业机构，3 - 库房
     */
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
