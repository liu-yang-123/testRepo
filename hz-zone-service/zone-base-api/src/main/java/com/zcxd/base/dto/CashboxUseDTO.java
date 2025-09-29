package com.zcxd.base.dto;

import lombok.Data;

import java.util.List;

/**
 * 
 * @ClassName CashboxDTO
 * @Description 钞盒信息
 * @author 秦江南
 * @Date 2021年5月24日上午9:51:18
 */
@Data
public class CashboxUseDTO {
    /**
     * 分配钞盒列表
     */
    List<Long> boxIds;

}
