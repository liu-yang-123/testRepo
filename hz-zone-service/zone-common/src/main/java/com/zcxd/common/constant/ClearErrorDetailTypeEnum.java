package com.zcxd.common.constant;

/**
 * 差错明细类型
 */
public enum ClearErrorDetailTypeEnum {
    FAKE(1,"假币"),
    BAD(2,"残币"),
    CARRY(3,"夹张");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    ClearErrorDetailTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        ClearErrorDetailTypeEnum[] businessModeEnums = values();
        for (ClearErrorDetailTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
