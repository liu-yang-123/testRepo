package com.zcxd.common.constant;

/**
 * 车辆类型
 */
public enum VehicleTypeEnum {
    TRUCK(1,"押运车"),
    CAR(2,"小轿车");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    VehicleTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        VehicleTypeEnum[] businessModeEnums = values();
        for (VehicleTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
