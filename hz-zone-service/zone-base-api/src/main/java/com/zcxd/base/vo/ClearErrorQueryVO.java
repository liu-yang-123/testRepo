package com.zcxd.base.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 清分差错审核DTO
 */
@Data
public class ClearErrorQueryVO {
    /**
     * 分页
     */
    @NotNull(message = "页码不能为空")
    private int page;
    /**
     * 每页条数
     */
    @NotNull(message = "条数不能为空")
    private int limit;
    /**
     * 部门
     */
    @NotNull(message = "部门不能为空")
    private long departmentId;
    /**
     * 银行机构
     */
    private Long bankId;
    /**
     * 查询开始日期
     */
    private Long dateBegin;
    /**
     * 查询结束日期
     */
    private Long dateEnd;
    /**
     * 券别
     */
    private Long denomId;
    /**
     * 发现人
     */
    private String findName;
}
