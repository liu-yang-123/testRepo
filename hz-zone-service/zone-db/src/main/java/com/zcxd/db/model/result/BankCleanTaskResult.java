package com.zcxd.db.model.result;

import lombok.Data;

/**
 * @author songanwei
 * @date 2021-06-29
 */
@Data
public class BankCleanTaskResult {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 银行机构ID
     */
    private Long bankId;

    /**
     * 状态
     */
    private Integer statusT;

    /**
     * 是否执行出库动作
     */
    private Integer isOut;
}
