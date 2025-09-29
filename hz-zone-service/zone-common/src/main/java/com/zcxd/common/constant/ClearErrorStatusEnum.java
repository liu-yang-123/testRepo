package com.zcxd.common.constant;

public enum ClearErrorStatusEnum {
    CREATE(0,"已创建"), //已创建
    REJECT(1,"审核拒绝"), //审核拒绝
    CONFIRM(2,"确认"), //确认
    APPROVE(3,"审核通过"), //审核通过
    CANCELING(4,"人工撤销申请中"), //人工撤销申请中
    CALCELED(5,"已撤销"); //已撤销
    private int value;

    private String text;

    public int getValue() {
        return this.value;
    }

    public static  String getText(Integer value) {
        for (ClearErrorStatusEnum errorStatusEnum : values()) {
            if (errorStatusEnum.getValue()==(value)) {
                return errorStatusEnum.text;
            }
        }
        return null;
    }
    ClearErrorStatusEnum(int value,String text) {
        this.value = value;
        this.text=text;
    }
}
