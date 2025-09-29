package com.zcxd.common.constant;

/**
 * 线路交接信息变更类型
 */
public enum RouteHandoverChangeEnum {
	BOX(0,"钞盒"),
    BAG(1,"钞袋");
	
    private Integer value;

    private String text;

    public Integer getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    RouteHandoverChangeEnum(final Integer value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(Integer value) {
        RouteHandoverChangeEnum[] businessModeEnums = values();
        for (RouteHandoverChangeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
