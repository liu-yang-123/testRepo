package com.zcxd.common.constant;

/**
 * 券别类型
 */
public enum DenomTypeEnum {
    USABLE(0,"可用券"),
    BAD(1,"残损券"),
    GOOD(2,"五好券"),
    UNCLEAR(3,"未清分"),
    REMNANT(4,"尾零钞");
    private int value;

    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    DenomTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        DenomTypeEnum[] businessModeEnums = values();
        for (DenomTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
