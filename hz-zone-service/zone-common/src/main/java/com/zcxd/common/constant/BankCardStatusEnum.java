package com.zcxd.common.constant;

/**
 * 银行卡状态
 */
public enum BankCardStatusEnum {
    RETRIVE(0,"回笼"), //业务员从ATM收回吞没卡
    COLLECT(1,"移交"), //业务员将吞没卡归集到调度员
    ALLOCATION(2,"分配"),//后台进行分配线路
    DISPATCH(3,"派送"), //调度员将吞没卡分派给线路业务员
    DELIVER(4,"上交"); //吞没卡上交给银行或客户自取
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    BankCardStatusEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        BankCardStatusEnum[] businessModeEnums = values();
        for (BankCardStatusEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
