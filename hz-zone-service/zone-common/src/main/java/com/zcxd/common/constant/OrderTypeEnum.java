package com.zcxd.common.constant;

/**
 * 出入库订单类型
 */
public enum OrderTypeEnum {
    STOCK_IN(0,"入库"),
    STOCK_OUT(1,"出库");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    OrderTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        OrderTypeEnum[] businessModeEnums = values();
        for (OrderTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
