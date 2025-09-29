package com.zcxd.base.dto;

import lombok.Data;

/**
 * 
 * @ClassName AtmTaskCheckDTO
 * @Description ATM巡检任务DTO
 * @author 秦江南
 * @Date 2021年6月15日下午2:18:14
 */
@Data
public class AtmTaskCheckDTO {

    /**
     * 检查项目结果
     */
    private CheckItemResultDTO checkItemResult;

    /**
     * 备注说明
     */
    private String comments;

}
