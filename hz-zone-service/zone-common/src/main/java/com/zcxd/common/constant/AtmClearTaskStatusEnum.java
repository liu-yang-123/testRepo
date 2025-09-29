package com.zcxd.common.constant;

/**
 * atm清分任务状态
 */
public enum AtmClearTaskStatusEnum {
    UNDO(0,"未执行"),
    FINISH(1,"已完成"),
    CANCEL(2,"已取消");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    AtmClearTaskStatusEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        AtmClearTaskStatusEnum[] businessModeEnums = values();
        for (AtmClearTaskStatusEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
