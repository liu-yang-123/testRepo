package com.zcxd.common.constant;

/**
 * 线路订单状态
 */
public enum RouteStatusEnum {
    CREATE(0,"新建"),
    CHECKED(1,"已审核"),
    DISPATCH(2,"配钞完成"),
    DISPATCH_CHECK(3,"配钞复核完成"),
    FINISH(4,"交接完成");
    private int value;

    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    RouteStatusEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        RouteStatusEnum[] businessModeEnums = values();
        for (RouteStatusEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
