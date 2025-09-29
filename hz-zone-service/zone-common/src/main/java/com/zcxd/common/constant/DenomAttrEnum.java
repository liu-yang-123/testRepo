package com.zcxd.common.constant;

public enum DenomAttrEnum {
    /**
     * 券别属性
     */
    PAPER("P","纸币"), //纸币
    COIN("C","硬币"); //硬币

    private String value;
    private String text;


    public String getValue() {
        return this.value;
    }

    DenomAttrEnum(String value,String text) {
        this.value = value;
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
    public static String getText(int value) {
        DenomAttrEnum[] attrEnums = values();
        for (DenomAttrEnum attrEnum : attrEnums) {
            if (attrEnum.getValue().equals(value)) {
                return attrEnum.getText();
            }
        }
        return "";
    }
}
