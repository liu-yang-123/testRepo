package com.zcxd.common.constant;

/**
 * atm运行状态
 */
public enum AtmRunStatusEnum {
    NORMAL(0,"正常"),
    NO_TRANS(1,"无存取款项"),
    HALF_NORMAL(2,"部分功能正常"),
    NO_SERVICE(3,"暂停服务");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    AtmRunStatusEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        AtmRunStatusEnum[] businessModeEnums = values();
        for (AtmRunStatusEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
