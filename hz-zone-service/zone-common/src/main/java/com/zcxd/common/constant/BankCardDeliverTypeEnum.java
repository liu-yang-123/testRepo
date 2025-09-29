package com.zcxd.common.constant;

/**
 * 银行卡上交方式状态
 */
public enum BankCardDeliverTypeEnum {
    UNDO(0,"未处理"), //未上缴
    TOBANK(1,"上交银行"),
    TOCUST(2,"现场取卡");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    BankCardDeliverTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        BankCardDeliverTypeEnum[] businessModeEnums = values();
        for (BankCardDeliverTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
