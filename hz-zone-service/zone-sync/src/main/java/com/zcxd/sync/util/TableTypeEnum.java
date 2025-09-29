package com.zcxd.sync.util;


/**
 * 数据表上传类别
 */
public enum TableTypeEnum {
    //员工信息
    clear_task(1,"员工信息"),
    //部门信息
    clear_record(2, "部门信息"),
    clear_denom(3,"券别"),   //券别
    clear_currency(4,"货币"),//货币
    clear_batch(5,"银行交款，送款"),   //银行交款，送款
    clear_batch_record(6,"银行送款交款明细"), //银行送款交款明细
    clear_banknode(7, "银行机构"),//银行机构
    employee(8, "出入库清单"), //员工信息
    dictionary(9, "数据字典"), //数据字典
    device_model(10, "设备型号"), //设备型号
    device_maintain(11, "设备维护记录"), //设备维护记录
    device_factory(12,"设备厂商"), //设备厂商
    device(13,"设备信息列表"), //设备信息列表
    department(14, "出入库明细"), //部门信息
    employee_job(15,"岗位定义"), //岗位定义
    department_task(16,"车间清分任务"), //车间清分任务
    employee_workload(17,"员工业务量统计"),//员工业务量统计
    clear_error(18,"清分差错明细信息"),//清分差错明细
    work_group(19,"清分业务小组"),//清分业务小组
    device_capacity(20,"理论产能数据");//理论产能
//    UNKNOW(999,"");

    private int value;
    private String text;
    public int getValue() {
        return this.value;
    }
    public String getText() {
        return this.text;
    }

    TableTypeEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }
    public static TableTypeEnum fromInt(int value) {
        TableTypeEnum[] as = TableTypeEnum.values();
        for (int i=0; i< as.length; i++) {
            if (as[i].value == value) {
                return as[i];
            }
        }
        return null;
    }
}
