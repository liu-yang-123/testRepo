package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName DictionaryDTO
 * @Description 数据字典
 * @author 秦江南
 * @Date 2021年5月14日上午9:41:32
 */
@Data
public class DictionaryDTO {
	/**
     * 唯一标识
     */
	private Long id;
	
    /**
     * 编号
     */
    private String code;

    /**
     * 分类名称
     */
    private String groups;

    /**
     * 内容
     */
    private String content;


    /**
     * 备注
     */
    private String comments;
}
