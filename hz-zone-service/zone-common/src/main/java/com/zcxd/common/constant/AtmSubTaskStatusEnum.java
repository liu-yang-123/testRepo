package com.zcxd.common.constant;

/**
 * atm任务状态
 */
public enum AtmSubTaskStatusEnum {
    CREATE(0,"未执行"),
    FINISH(1,"已完成");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    AtmSubTaskStatusEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        AtmSubTaskStatusEnum[] businessModeEnums = values();
        for (AtmSubTaskStatusEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
