package com.zcxd.common.annotation;

/**
 * @author songanwei
 * @date 2021/4/29 11:44
 */
public enum OperateType {

    /**
     * 添加
     */
    ADD(0),
    /**
     * 修改
     */
    EDIT(1),
    /**
     * 删除
     */
    DELETE(2);

    /**
     * 操作结果值
     */
    private int val;
    /**
     * 构造函数
     * @param iVal
     */
    OperateType(int iVal) {
        this.val = iVal;
    }
    /**
     * 返回处理结果值
     * @return 处理结果值
     */
    public int getVal() {
        return this.val;
    }
}
