package com.zcxd.common.constant;

/**
 * 对账结果类型
 */
public enum ClearErrorTypeEnum {
    NONE(0,"平账"),
    MORE(1,"长款"),
    LESS(2,"短款"),
    FAKE(3,"假币");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    ClearErrorTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        ClearErrorTypeEnum[] businessModeEnums = values();
        for (ClearErrorTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
