package com.zcxd.common.constant;

/**
 * pda用户角色类别
 */
public enum PdaUserRoleTypeEnum {
    UNKNOW(0,"未知"),
    STORE_KEEPER(1,"库管员"),
    CLEAR_MAN(2,"清分员"),
    BUSINESS_MAN(3,"清机员"),
    BANK_TELLER(4,"银行柜员"),
    SYSTEM_ADMIN(5,"系统管理员"),
    BANK_TELLER_ADMIN(6,"银行管理员");
    private int value;
    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    PdaUserRoleTypeEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        PdaUserRoleTypeEnum[] businessModeEnums = values();
        for (PdaUserRoleTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
