package com.zcxd.base.vo;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 清分任务查询VO
 * @ClassName TradeClearTaskVO
 * @Description TODO
 * @author 秦江南
 * @Date 2022年5月23日下午2:47:32
 */
@Data
public class TradeClearTaskQueryVO {
	/**
     * 页码
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
    private Long departmentId;
    
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
     * 清分类型
     */
    private Integer clearType;
    
}
