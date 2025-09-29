package com.zcxd.common.constant;

/**
 * 业务模式定义
 */
public enum BusinessModeEnum {
    ATM(1,"ATM加钞"),
    BOX(2,"尾箱"),
    CLEAR(3,"商业清分"),
    STORE(4,"金库");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    BusinessModeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        BusinessModeEnum[] businessModeEnums = values();
        for (BusinessModeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }

    public static BusinessModeEnum valueOf(int value) {
        BusinessModeEnum[] businessModeEnums = values();
        for (BusinessModeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum;
            }
        }
        return null;
    }
}
