package com.zcxd.voucher.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zccc
 */
@Data
public class ImportantVoucherNumberSegmentQueryVO {
    private Long id;
    /**
     * 重空名称
     */
    private String name;

    /**
     * 重空类型
     **/
    private Short type;

    /**
     * 重空Id
     */
    private Long voucherId;

    /**
     * 部门Id
     */
    @NotBlank(message = "部门Id不能为空")
    private Integer departmentId;
}
