package com.zcxd.common.constant;

/**
 * 出入库订单子业务类型
 */
public enum OrderSubTypeEnum {
    ATM(0,"ATM加钞"),
    TOBANK(1,"领缴款"),
    CLEAR(2,"清点"),
    AGENT(3,"代领缴");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    OrderSubTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        OrderSubTypeEnum[] businessModeEnums = values();
        for (OrderSubTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
