package com.zcxd.voucher.vo;

import lombok.Data;

/**
 * @author zccc
 */
@Data
public class ImportantVoucherEntryHouseVo {
    private Long voucherId;
    private Long targetBankId;
    private Long taskId;
    private Long start;
    private Long end;
    private String remark;
}
