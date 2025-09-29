package com.zcxd.pda.dto;

import lombok.Data;

import java.util.List;

/**
 * 网点分组atm任务列表DTO
 */
@Data
public class AtmTaskGroupDTO {

    /**
     * 银行网点id
     */
    private Long subBankId;

    /**
     * 网点编号
     */
    private String subBankNo;
    /**
     * 网点名称
     */
    private String subBankName;

    /**
     * 总行名称
     */
    private String bankName;

    /**
     * 任务列表
     */
    private List<AtmTaskGroupItemNew> atmTaskGroupItems;
}
