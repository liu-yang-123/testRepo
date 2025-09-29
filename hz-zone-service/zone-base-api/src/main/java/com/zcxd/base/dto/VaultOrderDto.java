package com.zcxd.base.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-05-29
 */
@Data
public class VaultOrderDto implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    private Long departmentId;

    /**
     * 机构ID
     */
    private Long bankId;

    /**
     * 机构名称
     */
    private String bankName;

    /**
     * 订单类别 0-入库 1-出库
     */
    private Integer orderType;

    /**
     * 订单类型 0-ATM加钞 1-领缴款
     */
    private Integer subType;

    /**
     * 单据日期
     */
    private Long orderDate;

    /**
     * 单据金额
     */
//    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal orderAmount;

    /**
     * 凭证图片
     */
    private String voucherUrl;

    /**
     * 审核人
     */
    private Long checkUser;

    /**
     * 审核时间
     */
    private Long checkTime;

    /**
     * 状态：录入、已审核
     */
    private Integer statusT;

    /**
     * 备注
     */
    private String comments;

    /**
     * 查库人1
     */
    private Long whOpMan;

    /**
     * 查库人1名称
     */
    private String whOpManName;

    /**
     * 查库人2
     */
    private Long whCheckMan;

    /**
     * 查库人2名称
     */
    private String whCheckManName;
    /**
     * 查库人3
     */
    private Long whConfirmMan;

    /**
     * 查库人3名称
     */
    private String whConfirmManName;

    /**
     * 盘点时间
     */
    private Long whOpTime;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 是否当前审核用户
     */
    private boolean audit;
}
