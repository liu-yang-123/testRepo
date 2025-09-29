package com.zcxd.common.constant;

/**
 * 数据删除状态
 */
public enum DeleteFlagEnum {
    YES(1,"是"),
    NOT(0,"否");
    private int value;

    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    DeleteFlagEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        DeleteFlagEnum[] businessModeEnums = values();
        for (DeleteFlagEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
