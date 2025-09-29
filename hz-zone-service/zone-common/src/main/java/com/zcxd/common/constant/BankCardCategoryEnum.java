package com.zcxd.common.constant;

/**
 * 吞没卡管理类型
 */
public enum BankCardCategoryEnum {
    CARD(0,"实物卡"),
    PAPER(1,"回执单");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    BankCardCategoryEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        BankCardCategoryEnum[] businessModeEnums = values();
        for (BankCardCategoryEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
