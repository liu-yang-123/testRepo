package com.zcxd.common.constant;

/**
 * 线路备用金记录状态
 */
public enum AtmAdditionCashStatusEnum {
    CREATE(0,"新建"),
    CONFIRM(1,"已确认"),
    OUTSTORE(2,"已出库"),
    CANCEL(3,"已撤销");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    AtmAdditionCashStatusEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        AtmAdditionCashStatusEnum[] businessModeEnums = values();
        for (AtmAdditionCashStatusEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
