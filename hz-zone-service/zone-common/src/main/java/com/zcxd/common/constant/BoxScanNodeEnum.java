package com.zcxd.common.constant;

/**
 * 钞盒扫描节点
 */
public enum BoxScanNodeEnum {
    PACK(0,"包装"),
    STOREIN(1,"入库"),
    STOREOUT(2,"出库"),
    CHECK(3,"盘点"),
    DISPATCH(4,"配钞"),
    DISPATCH_CONFIRM(5,"配钞复核"),
    CASHIN(6,"加钞"),
    RETURN(7,"原装钞盒重绑"),
    UNBIND(8,"手工解绑");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    BoxScanNodeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        BoxScanNodeEnum[] businessModeEnums = values();
        for (BoxScanNodeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
