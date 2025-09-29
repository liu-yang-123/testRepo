package com.zcxd.voucher.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author zccc
 */

public enum ImportantVoucherWarehouseItemOperateTypeEnum {
    ENTRY(1,"入库"),
    OUT(2,"出库");
    @EnumValue
    private final int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    ImportantVoucherWarehouseItemOperateTypeEnum(final int value, final String text) {
        this.value = value;
        this.text = text;
    }
}
