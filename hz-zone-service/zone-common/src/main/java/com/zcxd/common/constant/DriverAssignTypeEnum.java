package com.zcxd.common.constant;

/**
 * 司机排班类型
 */
public enum DriverAssignTypeEnum {
    RANDOM(0,"随机"),
    FIXED(1,"固定");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    DriverAssignTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        DriverAssignTypeEnum[] businessModeEnums = values();
        for (DriverAssignTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
