package com.zcxd.common.constant;

/**
 * 通行备案类型
 */
public enum PassTypeEnum {
    CLEAN_CODE(0,"清机密码备案"),
    SUB_BANK(1,"网点出入备案");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    PassTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        PassTypeEnum[] businessModeEnums = values();
        for (PassTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
