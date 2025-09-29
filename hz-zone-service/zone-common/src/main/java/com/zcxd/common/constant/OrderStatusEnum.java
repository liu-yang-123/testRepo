package com.zcxd.common.constant;

/**
 * 出入库订单状态
 */
public enum OrderStatusEnum {
    CANCELED(-1,"已撤销"), //新建
    CREATE(0,"新建"), //新建
    CHECKING(1,"审核中"), //
    CHECK_REJECT(2,"审核拒绝"), //
    CHECK_APPROVE(3,"审核通过"), //已审核
    FINISH(4,"完成"), //已
    CANCELING(5,"撤销中"), //已
    CANCEL_REJECT(6,"撤销拒绝"); //已
    private int value;

    private String text;

    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    OrderStatusEnum(final int value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(int value) {
        OrderStatusEnum[] businessModeEnums = values();
        for (OrderStatusEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
