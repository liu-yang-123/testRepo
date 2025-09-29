package com.zcxd.common.constant;

/**
 * atm任务类型
 */
public enum AtmBackupFlagEnum {
    PLAN(0,"预排"),
    BACKUP(1,"备用金");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    AtmBackupFlagEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        AtmBackupFlagEnum[] businessModeEnums = values();
        for (AtmBackupFlagEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
