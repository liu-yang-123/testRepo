package com.zcxd.common.constant;

/**
 * 回笼现金装运方式
 */
public enum CarryTypeEnum {
    BAG(0,"钞袋"),
    BOX(1,"钞盒");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    CarryTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        CarryTypeEnum[] businessModeEnums = values();
        for (CarryTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
