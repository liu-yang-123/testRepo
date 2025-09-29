package com.zcxd.common.constant;

/**
 * atm任务类型
 */
public enum AtmTaskTypeEnum {
    REPAIR(1,"维修"),
    CASHIN(2,"加钞"),
    CLEAN(3, "清机"),
    CHECK(4,"巡检"),
    REMOVE(5,"撤机");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    AtmTaskTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        AtmTaskTypeEnum[] businessModeEnums = values();
        for (AtmTaskTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
