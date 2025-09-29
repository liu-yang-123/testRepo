package com.zcxd.voucher.vo;

import lombok.Data;

/**
 * @author zccc
 */
@Data
public class ImportantVoucherQueryVO {
    /**
     * id
     */
    private Long id;
    /**
     * 所属银行名称
     */
    private String bankName;

    /**
     * 所属银行Id
     */
    private String bankId;

    /**
     * 重空名称
     */
    private String name;

    /**
     * 重空类型
     */
    private Integer type;

    /**
     * 部门ID
     */
    private Integer departmentId;
}
