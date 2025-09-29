package com.zcxd.common.constant;

/**
 * 岗位类型
 */
public enum JobTypeEnum {
	OTHER(0,"其它"),
    DRIVER(1,"司机岗"),
    GUARD(2,"护卫岗"),
	KEY(3,"钥匙岗"),
	CLEAN(4,"密码岗"),
	CLEAR(5,"清点岗"),
	STORAGE(6,"库管岗");
	
    private Integer value;

    private String text;

    public Integer getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    JobTypeEnum(final Integer value, final String text) {
        this.value = value;
        this.text=text;
    }

    public static String getText(Integer value) {
        JobTypeEnum[] businessModeEnums = values();
        for (JobTypeEnum businessModeEnum : businessModeEnums) {
            if (businessModeEnum.getValue()==value) {
                return businessModeEnum.getText();
            }
        }
        return "";
    }
}
