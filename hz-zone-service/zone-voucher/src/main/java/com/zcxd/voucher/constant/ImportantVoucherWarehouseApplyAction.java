package com.zcxd.voucher.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum ImportantVoucherWarehouseApplyAction {
    EDIT(1,"编辑"),
    APPROVAL(2,"审批"),
    CANCEL(3, "反审"),
    DELETE(4, "删除");
    @EnumValue
    private final int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    ImportantVoucherWarehouseApplyAction(final int value, final String text) {
        this.value = value;
        this.text = text;
    }

    public static boolean containValue(String str) {
        return EDIT.getText().equals(str) || APPROVAL.getText().equals(str) || CANCEL.getText().equals(str) || DELETE.getText().equals(str);
    }
}
