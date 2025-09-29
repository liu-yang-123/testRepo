package com.zcxd.base.vo;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 
 * @ClassName FileRecordQueryVO
 * @Description 文件传输记录查询
 * @author 秦江南
 * @Date 2022年11月14日上午10:35:06
 */
@Data
public class FileRecordQueryVO {
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
     * 标题
     */
    private String recordTitle;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 单位id
     */
    private Long companyId;
    
    /**
     * 查询开始日期
     */
    private Long dateBegin;
    
    /**
     * 查询结束日期
     */
    private Long dateEnd;
    
    
}
