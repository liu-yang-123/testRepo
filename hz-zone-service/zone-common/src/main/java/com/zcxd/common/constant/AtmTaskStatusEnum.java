package com.zcxd.common.constant;

/**
 * atm任务状态
 */
public enum AtmTaskStatusEnum {
    CANCEL(-1,"已取消"),
    CREATE(0,"已创建"),
    CONFRIM(1,"已确认"),
    FINISH(2,"已完成");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    AtmTaskStatusEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        AtmTaskStatusEnum[] businessModeEnums = values();
        for (AtmTaskStatusEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
